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
import fi.helsinki.opintoni.dto.EventDtoBuilder;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.optime.OptimeService;
import fi.helsinki.opintoni.integration.studyregistry.Event;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryService;
import fi.helsinki.opintoni.service.converter.EventConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EventService {

    private final StudyRegistryService studyRegistryService;
    private final CoursePageClient coursePageClient;
    private final CourseService courseService;
    private final EventConverter eventConverter;
    private final OptimeConfiguration optimeConfiguration;
    private final OptimeService optimeService;
    private final OptimeCalendarService optimeCalendarService;

    @Autowired
    public EventService(StudyRegistryService studyRegistryService,
                        CoursePageClient coursePageClient,
                        CourseService courseService,
                        EventConverter eventConverter,
                        OptimeConfiguration optimeConfiguration,
                        OptimeService optimeService,
                        OptimeCalendarService optimeCalendarService) {

        this.studyRegistryService = studyRegistryService;
        this.coursePageClient = coursePageClient;
        this.courseService = courseService;
        this.eventConverter = eventConverter;
        this.optimeConfiguration = optimeConfiguration;
        this.optimeService = optimeService;
        this.optimeCalendarService = optimeCalendarService;
    }

    public List<EventDto> getStudentEvents(String studentNumber, Locale locale) {
        return filterEnrichAndMergeEvents(
            studyRegistryService.getStudentEvents(studentNumber),
            Collections.emptyList(),
            courseService.getStudentCourseIds(studentNumber),
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

        return filterEnrichAndMergeEvents(
            studyRegistryEvents,
            optimeEvents,
            courseService.getTeacherCourseIds(teacherNumber),
            locale);
    }

    private List<EventDto> filterEnrichAndMergeEvents(
        List<Event> studyRegistryEvents,
        List<EventDto> optimeEvents,
        List<String> courseIds,
        Locale locale) {

        Map<String, CoursePageCourseImplementation> coursePages = getCoursePages(studyRegistryEvents, courseIds, locale);

        Stream<EventDto> studyRegistryEventDtos = studyRegistryEvents.stream()
            .filter(event -> !event.isCancelled && event.startDate != null)
            .map(event -> eventConverter.toDto(event, getCoursePage(coursePages, getRealisationId(event)), locale));

        Stream<EventDto> optimeEventDtos = optimeEvents.stream();

        Stream<EventDto> coursePageEventDtos = coursePages.values().stream()
            .flatMap(c -> c.events.stream()
                .filter(e -> e.begin != null)
                .map(e -> eventConverter.toDto(e, c)));

        return mergeStreams(Stream.concat(studyRegistryEventDtos, optimeEventDtos), coursePageEventDtos);
    }

    private List<EventDto> mergeStreams(Stream<EventDto> s1, Stream<EventDto> s2) {
        return Stream
            .concat(s1, s2)
            .collect(Collectors.toMap(EventDto::getRealisationIdAndTimes, Function.identity(), (a, b) -> new EventDtoBuilder()
                .setType(a.type)
                .setSource(getEventSource(a, b))
                .setStartDate(a.startDate)
                .setEndDate(a.endDate)
                .setRealisationId(a.realisationId)
                .setTitle(a.title)
                .setCourseTitle(a.courseTitle)
                .setCourseUri(a.courseUri)
                .setCourseImageUri(a.courseImageUri)
                .setCourseMaterialDto(a.courseMaterial)
                .setMoodleUri(a.moodleUri)
                .setHasMaterial(a.hasMaterial)
                .setLocations(Stream.concat(a.locations.stream(), b.locations.stream()).collect(Collectors.toList()))
                .setOptimeExtras(a.optimeExtras != null ? a.optimeExtras : b.optimeExtras)
                .createEventDto())).values().stream()
            .sorted()
            .collect(Collectors.toList());
    }

    private EventDto.Source getEventSource(EventDto lhs, EventDto rhs) {
        if (lhs.source == EventDto.Source.STUDY_REGISTRY || rhs.source == EventDto.Source.STUDY_REGISTRY) {
            return EventDto.Source.STUDY_REGISTRY;
        } else if (lhs.source == EventDto.Source.OPTIME || rhs.source == EventDto.Source.OPTIME) {
            return EventDto.Source.OPTIME;
        }
        return EventDto.Source.COURSE_PAGE;
    }

    private CoursePageCourseImplementation getCoursePage(Map<String, CoursePageCourseImplementation> coursePages, String realisationId) {
        return Optional
            .ofNullable(coursePages.get(realisationId))
            .orElseGet(CoursePageCourseImplementation::new);
    }

    private String getRealisationId(Event event) {
        return String.valueOf(event.realisationId);
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
