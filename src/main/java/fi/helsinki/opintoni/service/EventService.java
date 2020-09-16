/*
 * This file is part of MystudiesMyteaching application.
 *
 * MystudiesMyteaching application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MystudiesMyteaching application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MystudiesMyteaching application.  If not, see <http://www.gnu.org/licenses/>.
 */

package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.config.OptimeConfiguration;
import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.integration.IntegrationUtil;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsClient;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsCourseUnitRealisation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.optime.OptimeService;
import fi.helsinki.opintoni.integration.sotka.SotkaClient;
import fi.helsinki.opintoni.integration.studyregistry.CourseRealisation;
import fi.helsinki.opintoni.integration.studyregistry.Enrollment;
import fi.helsinki.opintoni.integration.studyregistry.Event;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryService;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.service.converter.EventConverter;
import fi.helsinki.opintoni.util.CoursePageUtil;
import fi.helsinki.opintoni.util.DateTimeUtil;
import fi.helsinki.opintoni.util.EventUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EventService {

    private final StudyRegistryService studyRegistryService;
    private final CoursePageClient coursePageClient;
    private final CourseCmsClient courseCmsClient;
    private final SotkaClient sotkaClient;
    private final CoursePageUtil coursePageUtil;
    private final EventConverter eventConverter;
    private final OptimeConfiguration optimeConfiguration;
    private final OptimeService optimeService;
    private final OptimeCalendarService optimeCalendarService;

    @Autowired
    public EventService(StudyRegistryService studyRegistryService,
                        CoursePageClient coursePageClient,
                        CourseCmsClient courseCmsClient,
                        SotkaClient sotkaClient,
                        CoursePageUtil coursePageUtil,
                        EventConverter eventConverter,
                        OptimeConfiguration optimeConfiguration,
                        OptimeService optimeService,
                        OptimeCalendarService optimeCalendarService) {

        this.studyRegistryService = studyRegistryService;
        this.coursePageClient = coursePageClient;
        this.courseCmsClient = courseCmsClient;
        this.sotkaClient = sotkaClient;
        this.coursePageUtil = coursePageUtil;
        this.eventConverter = eventConverter;
        this.optimeConfiguration = optimeConfiguration;
        this.optimeService = optimeService;
        this.optimeCalendarService = optimeCalendarService;
    }

    public List<EventDto> getStudentEvents(String studentNumber, Locale locale) {
        List<Enrollment> enrollments = studyRegistryService.getEnrollments(studentNumber).stream()
            .filter(e -> !e.isCancelled)
            .collect(Collectors.toList());

        return filterEnrichAndMergeEvents(
            studyRegistryService.getStudentEvents(studentNumber),
            Collections.emptyList(),
            enrollments,
            locale);
    }

    public List<EventDto> getTeacherEvents(final String teacherNumber, final Locale locale) {

        List<EventDto> optimeEvents = Collections.emptyList();
        List<Event> studyRegistryEvents = Collections.emptyList();

        if (optimeConfiguration.useOptimeFeedForWebCalendar) {
            String optimeFeedurl = optimeCalendarService.getOptimeCalendar(teacherNumber).url;
            optimeEvents = optimeService.getOptimeEvents(optimeFeedurl);
        } else {
            studyRegistryEvents = studyRegistryService.getTeacherEvents(teacherNumber);
        }

        List<TeacherCourse> courses = studyRegistryService.getTeacherCourses(teacherNumber, DateTimeUtil.getSemesterStartDate(LocalDate.now()))
            .stream()
            .filter(c -> !c.isCancelled)
            .collect(Collectors.toList());

        return filterEnrichAndMergeEvents(
            studyRegistryEvents,
            optimeEvents,
            courses,
            locale);
    }

    private List<EventDto> filterEnrichAndMergeEvents(
        List<Event> studyRegistryEvents,
        List<EventDto> optimeEvents,
        List<? extends CourseRealisation> courses,
        Locale locale) {

        Map<Boolean, List<CourseRealisation>> partitioned = courses.stream()
            .collect(Collectors.partitioningBy(coursePageUtil::useNewCoursePageIntegration));

        List<String> useOld = partitioned.get(false).stream().map(cr -> {
            return IntegrationUtil.stripKnownSisuCurPrefixes(cr.realisationId);
        }).collect(Collectors.toList());

        /// XXX TODO use sotka here
        Map<String, CoursePageCourseImplementation> coursePages = getOldCoursePages(studyRegistryEvents, useOld, locale);

        Map<String, CourseCmsCourseUnitRealisation> newCoursePages = partitioned.get(true).stream()
            .map(c -> c.realisationId)
            .distinct()
            .map(realisationId -> {
                return Map.entry(realisationId, courseCmsClient.getCoursePage(realisationId, locale));
            })
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        Stream<EventDto> studyRegistryEventDtos = studyRegistryEvents.stream()
            .filter(event -> !event.isCancelled && event.startDate != null)
            .map(event ->
                eventConverter.toDto(
                    event,
                    getCoursePage(coursePages, event.realisationId),
                    getNewCoursePage(newCoursePages, event.realisationId),
                    locale));

        Stream<EventDto> optimeEventDtos = optimeEvents.stream();

        Stream<EventDto> coursePageEventDtos = coursePages.values().stream()
            .flatMap(c -> c.events.stream()
                .filter(e -> e.begin != null)
                .map(e -> eventConverter.toDto(e, c)));
        return EventUtils.mergeStreams(Stream.concat(studyRegistryEventDtos, optimeEventDtos), coursePageEventDtos);
    }

    private CoursePageCourseImplementation getCoursePage(Map<String, CoursePageCourseImplementation> coursePages, String realisationId) {
        return coursePages.getOrDefault(realisationId, new CoursePageCourseImplementation());
    }

    private CourseCmsCourseUnitRealisation getNewCoursePage(Map<String, CourseCmsCourseUnitRealisation> coursePages,
        String realisationId) {
        return coursePages.getOrDefault(realisationId, null);
    }

    private Map<String, CoursePageCourseImplementation> getOldCoursePages(
        List<Event> events,
        List<String> courseIds,
        Locale locale) {
        return Stream
            .concat(
                getEventCourseIds(events),
                courseIds.stream())
            .distinct()
            .collect(Collectors.toMap(
                realisationId -> realisationId,
                courseImplementationId -> coursePageClient.getCoursePage(courseImplementationId, locale)));
    }

    private Stream<String> getEventCourseIds(List<Event> events) {
        return events
            .stream()
            .map(event -> event.realisationId);
    }

}
