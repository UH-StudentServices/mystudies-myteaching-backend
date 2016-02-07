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
        List<String> courseIds,
        Locale locale) {

        Map<String, CoursePageCourseImplementation> coursePages = getCoursePages(oodiEvents, courseIds);

        Stream<CoursePageEvent> coursePageEvents = getCoursePageEvents(courseIds);

        Stream<EventDto> oodiEventDtos = oodiEvents.stream()
            .map(oodiEvent -> eventConverter.toDto(oodiEvent, getCoursePage(coursePages, getRealisationId(oodiEvent)), locale));

        Stream<EventDto> coursePageEventDtos = coursePageEvents
            .map(coursePageEvent -> eventConverter.toDto(coursePageEvent, getCoursePage(coursePages, getRealisationId(coursePageEvent))));

        return Stream
            .concat(oodiEventDtos, coursePageEventDtos)
            .sorted()
            .collect(Collectors.toList());
    }

    private CoursePageCourseImplementation getCoursePage(Map<String, CoursePageCourseImplementation> coursePages, String realisationId) {
        return Optional
            .ofNullable(coursePages.get(realisationId))
            .orElseGet(() -> new CoursePageCourseImplementation());
    }

    private String getRealisationId(OodiEvent oodiEvent) {
        return String.valueOf(oodiEvent.realisationId);
    }

    private String getRealisationId(CoursePageEvent coursePageEvent) {
        return String.valueOf(coursePageEvent.courseImplementationId);
    }

    private Map<String, CoursePageCourseImplementation> getCoursePages(
        List<OodiEvent> oodiEvents,
        List<String> courseIds) {
        return Stream
                .concat(
                    getOodiEventCourseIds(oodiEvents),
                    courseIds.stream())
                .distinct()
                .collect(Collectors.toMap(
                    realisationId -> realisationId,
                    coursePageClient::getCoursePage));
    }

    private Stream<String> getOodiEventCourseIds(List<OodiEvent> oodiEvents) {
        return oodiEvents
            .stream()
            .map(oodiEvent -> String.valueOf(oodiEvent.realisationId));
    }

    private Stream<CoursePageEvent> getCoursePageEvents(List<String> courseIds) {
        return
            courseIds.stream()
                .flatMap(courseId -> coursePageClient.getEvents(courseId).stream());
    }

}