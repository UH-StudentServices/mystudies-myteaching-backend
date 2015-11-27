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
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.resolver.EventTypeResolver;
import fi.helsinki.opintoni.service.converter.CourseConverter;
import fi.helsinki.opintoni.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final OodiClient oodiClient;
    private final CourseConverter courseConverter;
    private final EventTypeResolver eventTypeResolver;

    @Autowired
    public CourseService(OodiClient oodiClient,
                         CourseConverter courseConverter,
                         EventTypeResolver eventTypeResolver) {
        this.oodiClient = oodiClient;
        this.courseConverter = courseConverter;
        this.eventTypeResolver = eventTypeResolver;
    }

    public List<CourseDto> getTeacherCourses(String teacherNumber, Locale locale) {
        return oodiClient.getTeacherCourses(teacherNumber, locale, DateTimeUtil.getSemesterStartDateString(LocalDate.now())).stream()
            .filter(oodiTeacherCourse -> !eventTypeResolver.isExam(oodiTeacherCourse.realisationTypeCode))
            .map(courseConverter::toDto)
            .collect(Collectors.toList());
    }

    public List<CourseDto> getStudentCourses(String studentNumber, Locale locale) {
        return oodiClient.getEnrollments(studentNumber, locale).stream()
            .filter(oodiEnrollment -> !eventTypeResolver.isExam(oodiEnrollment.typeCode))
            .map(c -> courseConverter.toDto(c, locale))
            .collect(Collectors.toList());
    }


    public Set<CourseDto> getCourses(Optional<String> studentNumber, Optional<String> teacherNumber, Locale locale) {
        Set<CourseDto> courseDtos = new HashSet<>();

        teacherNumber.ifPresent(number -> courseDtos.addAll(
                getTeacherCourses(number, locale))
        );

        studentNumber.ifPresent(number -> courseDtos.addAll(
                getStudentCourses(number, locale))
        );

        return courseDtos;
    }

}