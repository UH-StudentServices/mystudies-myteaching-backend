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
import fi.helsinki.opintoni.integration.coursecms.CourseCmsCourseUnitRealisation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.optime.OptimeService;
import fi.helsinki.opintoni.integration.studyregistry.CourseRealisation;
import fi.helsinki.opintoni.integration.studyregistry.Event;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryService;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.service.converter.EventConverter;
import fi.helsinki.opintoni.util.CoursePageUtil;
import fi.helsinki.opintoni.util.DateTimeUtil;
import fi.helsinki.opintoni.util.EventUtils;
import fi.helsinki.opintoni.util.FunctionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EventService {

    private final StudyRegistryService studyRegistryService;
    private final CoursePageUtil coursePageUtil;
    private final EventConverter eventConverter;
    private final OptimeConfiguration optimeConfiguration;
    private final OptimeService optimeService;
    private final OptimeCalendarService optimeCalendarService;

    @Autowired
    public EventService(StudyRegistryService studyRegistryService,
                        CoursePageUtil coursePageUtil,
                        EventConverter eventConverter,
                        OptimeConfiguration optimeConfiguration,
                        OptimeService optimeService,
                        OptimeCalendarService optimeCalendarService) {

        this.studyRegistryService = studyRegistryService;
        this.coursePageUtil = coursePageUtil;
        this.eventConverter = eventConverter;
        this.optimeConfiguration = optimeConfiguration;
        this.optimeService = optimeService;
        this.optimeCalendarService = optimeCalendarService;
    }

    public List<EventDto> getTeacherEvents(final String hloId, final Locale locale) {

        List<EventDto> optimeEvents = Collections.emptyList();
        List<Event> studyRegistryEvents = Collections.emptyList();

        if (optimeConfiguration.useOptimeFeedForWebCalendar) {
            String optimeFeedurl = optimeCalendarService.getOptimeCalendar(hloId).url;
            optimeEvents = optimeService.getOptimeEvents(optimeFeedurl);
        } else {
            studyRegistryEvents = studyRegistryService.getTeacherEvents(hloId);
        }

        List<TeacherCourse> courses = studyRegistryService.getTeacherCourses(hloId, DateTimeUtil.getSemesterStartDate(LocalDate.now()))
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

        Map<String, String> coursePageUrls = coursePageUtil.getCoursePageUrls(courses, locale);
        Map<String, CoursePageCourseImplementation> oldCoursePages = coursePageUtil.getOldCoursePages(coursePageUrls, locale);
        Map<String, CourseCmsCourseUnitRealisation> newCoursePages = coursePageUtil.getNewCoursePages(coursePageUrls, locale);

        Stream<EventDto> studyRegistryEventDtos = studyRegistryEvents.stream()
            .filter(event -> !event.isCancelled && event.startDate != null)
            .map(FunctionHelper.logAndIgnoreExceptions(event -> this.toEventDTO(event, oldCoursePages, newCoursePages, coursePageUrls, locale)))
            .filter(Objects::nonNull);

        Stream<EventDto> optimeEventDtos = optimeEvents.stream();
        Stream<EventDto> coursePageEventDtos = oldCoursePages.values().stream()
            .flatMap(c -> c.events.stream()
                .filter(e -> e.begin != null)
                .map(FunctionHelper.logAndIgnoreExceptions(e -> eventConverter.toDto(e, c)))
                .filter(Objects::nonNull)
            );

        return EventUtils.mergeStreams(Stream.concat(studyRegistryEventDtos, optimeEventDtos), coursePageEventDtos);
    }

    private EventDto toEventDTO(Event event, Map<String, CoursePageCourseImplementation> coursePages,
        Map<String, CourseCmsCourseUnitRealisation> newCoursePages, Map<String, String> coursePageUrls, Locale locale) {
        return eventConverter.toDto(
            event,
            getCoursePage(coursePages, event.realisationId),
            getNewCoursePage(newCoursePages, event.realisationId),
            coursePageUrls.get(event.realisationId),
            locale);
    }

    private CoursePageCourseImplementation getCoursePage(Map<String, CoursePageCourseImplementation> coursePages, String realisationId) {
        return coursePages.getOrDefault(realisationId, new CoursePageCourseImplementation());
    }

    private CourseCmsCourseUnitRealisation getNewCoursePage(Map<String, CourseCmsCourseUnitRealisation> coursePages,
        String realisationId) {
        return coursePages.getOrDefault(realisationId, null);
    }

}
