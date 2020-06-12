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
import fi.helsinki.opintoni.integration.studyregistry.oodi.courseunitrealisation.Position;
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
import java.util.Optional;
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

    private static class Pair<X, Y> {

        public X x;
        public Y y;

        public Pair(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }

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

    public List<EventDto> getTeacherEvents(String teacherNumber, Locale locale) {

        List<EventDto> optimeEvents = Collections.emptyList();
        List<Event> studyRegistryEvents = Collections.emptyList();

        if (optimeConfiguration.useOptimeFeedForWebCalendar) {
            String optimeFeedurl = optimeCalendarService.getOptimeCalendar(teacherNumber).url;
            optimeEvents = optimeService.getOptimeEvents(optimeFeedurl);
        } else {
            studyRegistryEvents = studyRegistryService.getTeacherEvents(teacherNumber);
        }

        List<TeacherCourse> courses = studyRegistryService.getTeacherCourses(teacherNumber, DateTimeUtil.getSemesterStartDateString(LocalDate.now()))
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

        List<String> courseIds = courses.stream()
            .map(c -> c.realisationId)
            .collect(Collectors.toList());

        Map<String, CoursePageCourseImplementation> coursePages = getCoursePages(studyRegistryEvents, courseIds, locale);

        Map<String, CourseCmsCourseUnitRealisation> newCoursePages = courses.stream()
            .filter(coursePageUtil::useNewCoursePageIntegration)
            .map(c -> Position.getByValue(c.position).equals(Position.ROOT) ? c.realisationId : c.rootId)
            .distinct()
            .map(courseId -> {
                String optimeId = sotkaClient.getOodiHierarchy(courseId).optimeId;
                return new Pair<>(courseId, courseCmsClient.getCoursePage(optimeId != null ? optimeId : courseId, locale));
            })
            .collect(Collectors.toMap(p -> p.x, p -> p.y));

        Stream<EventDto> studyRegistryEventDtos = studyRegistryEvents.stream()
            .filter(event -> !event.isCancelled && event.startDate != null)
            .map(event ->
                eventConverter.toDto(
                    event,
                    getCoursePage(coursePages, getRealisationId(event)),
                    getNewCoursePage(newCoursePages, getRealisationRootId(event, courses)),
                    locale));

        Stream<EventDto> optimeEventDtos = optimeEvents.stream();

        Stream<EventDto> coursePageEventDtos = coursePages.values().stream()
            .flatMap(c -> c.events.stream()
                .filter(e -> e.begin != null)
                .map(e -> eventConverter.toDto(e, c)));

        return EventUtils.mergeStreams(Stream.concat(studyRegistryEventDtos, optimeEventDtos), coursePageEventDtos);
    }

    private CoursePageCourseImplementation getCoursePage(Map<String, CoursePageCourseImplementation> coursePages, String realisationId) {
        return Optional
            .ofNullable(coursePages.get(realisationId))
            .orElseGet(CoursePageCourseImplementation::new);
    }

    private CourseCmsCourseUnitRealisation getNewCoursePage(Map<String, CourseCmsCourseUnitRealisation> coursePages, String realisationId) {
        return coursePages.getOrDefault(realisationId, null);
    }

    private String getRealisationId(Event event) {
        return String.valueOf(event.realisationId);
    }

    private String getRealisationRootId(Event event, List<? extends CourseRealisation> courses) {
        String eventRealisationId = getRealisationId(event);
        return courses.stream()
            .filter(c -> String.valueOf(c.realisationId).equals(eventRealisationId))
            .findFirst()
            .map(courseRealisation ->
                Position.getByValue(courseRealisation.position).equals(Position.ROOT)
                    ? courseRealisation.realisationId : courseRealisation.rootId)
            .orElse("");
    }

    private Map<String, CoursePageCourseImplementation> getCoursePages(
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
            .map(event -> String.valueOf(event.realisationId));
    }

}
