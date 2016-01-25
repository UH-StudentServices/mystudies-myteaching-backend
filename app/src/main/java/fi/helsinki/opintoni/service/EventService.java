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

import fi.helsinki.opintoni.dto.CourseDto;
import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.service.converter.EventConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
        return getEvents(studentNumber, locale, this::getOodiStudentEvents, this::getCoursePageStudentEvents);
    }

    public List<EventDto> getTeacherEvents(String teacherNumber, Locale locale) {
        return getEvents(teacherNumber, locale, this::getOodiTeacherEvents, this::getCoursePageTeacherEvents);
    }

    @SafeVarargs
    private final List<EventDto> getEvents(String id, Locale locale, BiFunction<String, Locale, List<EventDto>>...
        eventProvides) {
        return Arrays.asList(eventProvides)
            .stream()
            .flatMap(p -> p.apply(id, locale).stream())
            .sorted()
            .collect(Collectors.toList());
    }

    private List<EventDto> getOodiTeacherEvents(String teacherNumber, Locale locale) {
        return oodiClient.getTeacherEvents(teacherNumber, locale).stream()
            .map(e -> eventConverter.toDto(e, locale))
            .collect(Collectors.toList());
    }

    private List<EventDto> getCoursePageTeacherEvents(String teacherNumber, Locale locale) {
        return getCoursePageEventsForCourses(courseService.getTeacherCourses(teacherNumber, locale));
    }

    private List<EventDto> getCoursePageStudentEvents(String studentNumber, Locale locale) {
        return getCoursePageEventsForCourses(courseService.getStudentCourses(studentNumber, locale));
    }

    private List<EventDto> getCoursePageEventsForCourses(List<CourseDto> courses) {
        return courses
            .stream()
            .map(course -> course.realisationId)
            .distinct()
            .filter(StringUtils::isNotEmpty)
            .flatMap(realisationId -> coursePageClient.getEvents(realisationId).stream())
            .map(eventConverter::toDto)
            .collect(Collectors.toList());
    }

    private List<EventDto> getOodiStudentEvents(String studentNumber, Locale locale) {
        return oodiClient.getStudentEvents(studentNumber, locale).stream()
            .map(e -> eventConverter.toDto(e, locale))
            .collect(Collectors.toList());
    }

}