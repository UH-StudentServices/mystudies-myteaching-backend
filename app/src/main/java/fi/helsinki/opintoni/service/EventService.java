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

import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageEvent;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiEvent;
import fi.helsinki.opintoni.service.converter.EventConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EventService {

    private final OodiClient oodiClient;
    private final CoursePageClient coursePageClient;
    private final CourseService courseService;
    private final EventConverter eventConverter;

    @Autowired
    public EventService(OodiClient oodiClient,
                        CoursePageClient coursePageClient,
                        CourseService courseService,
                        EventConverter eventConverter) {

        this.oodiClient = oodiClient;
        this.coursePageClient = coursePageClient;
        this.courseService = courseService;
        this.eventConverter = eventConverter;
    }

    public List<EventDto> getStudentEvents(String studentNumber, Locale locale) {
        return getEvents(
            oodiClient.getStudentEvents(studentNumber),
            courseService.getStudentCourseIds(studentNumber),
            locale);
    }

    public List<EventDto> getTeacherEvents(String teacherNumber, Locale locale) {
        return getEvents(
            oodiClient.getTeacherEvents(teacherNumber),
            courseService.getTeacherCourseIds(teacherNumber),
            locale);
    }

    private List<EventDto> getEvents(
        List<OodiEvent> oodiEvents,
        Stream<String> courseIds,
        Locale locale) {

        List<String> filteredCourseIds = filterOutCancelledCourses(courseIds);

        Map<String, CoursePageCourseImplementation> coursePages = getCoursePages(oodiEvents, filteredCourseIds, locale);

        Stream<EventDto> oodiEventDtos = oodiEvents.stream()
            .filter(oodiEvent -> !oodiEvent.isCancelled)
            .map(oodiEvent -> eventConverter.toDto(oodiEvent, getCoursePage(coursePages, getRealisationId(oodiEvent)), locale));

        Stream<EventDto> coursePageEventDtos = coursePages.values().stream().flatMap(c -> c.events.stream().map(e -> eventConverter.toDto(e, c)));

        return Stream
            .concat(oodiEventDtos, coursePageEventDtos)
            .collect(Collectors.groupingBy((EventDto dto) -> EventDto.getRealisationIdAndTimes(dto),
                Collectors.collectingAndThen(
                    Collectors.reducing((a,b) ->
                        new EventDto(
                            a.type,
                            a.source,
                            a.startDate,
                            a.endDate,
                            a.realisationId,
                            a.title,
                            a.courseTitle,
                            a.courseUri,
                            a.courseImageUri,
                            a.courseMaterial,
                            a.moodleUri,
                            a.hasMaterial,
                            a.locations,
                            b.locations
                        )
                    ),
                    Optional::get
                )
            )).values().stream()
            .sorted()
            .collect(Collectors.toList());
    }

    private CoursePageCourseImplementation getCoursePage(Map<String, CoursePageCourseImplementation> coursePages, String realisationId) {
        return Optional
            .ofNullable(coursePages.get(realisationId))
            .orElseGet(CoursePageCourseImplementation::new);
    }

    private String getRealisationId(OodiEvent oodiEvent) {
        return String.valueOf(oodiEvent.realisationId);
    }

    private Map<String, CoursePageCourseImplementation> getCoursePages(
        List<OodiEvent> oodiEvents,
        List<String> courseIds,
        Locale locale) {
        return Stream
                .concat(
                    getOodiEventCourseIds(oodiEvents),
                    courseIds.stream())
                .distinct()
                .collect(Collectors.toMap(
                    realisationId -> realisationId,
                    courseImplementationId -> coursePageClient.getCoursePage(courseImplementationId, locale)));
    }

    private Stream<String> getOodiEventCourseIds(List<OodiEvent> oodiEvents) {
        return oodiEvents
            .stream()
            .map(oodiEvent -> String.valueOf(oodiEvent.realisationId));
    }

    private List<String> filterOutCancelledCourses(Stream<String> courseIds) {
        return courseIds
            .filter(courseId -> !oodiClient.getCourseUnitRealisation(courseId).isCancelled)
            .collect(Collectors.toList());
    }

}
