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
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryService;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.service.converter.CourseConverter;
import fi.helsinki.opintoni.util.FunctionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class CourseService {

    private final StudyRegistryService studyRegistryService;
    private final CourseConverter courseConverter;

    @Autowired
    public CourseService(StudyRegistryService studyRegistryService,
                         CourseConverter courseConverter) {
        this.studyRegistryService = studyRegistryService;
        this.courseConverter = courseConverter;
    }

    public List<CourseDto> getTeacherCourses(String teacherNumber, Locale locale) {
        List<TeacherCourse> teacherCourses = studyRegistryService
            .getTeacherCourses(teacherNumber, LocalDate.now(ZoneId.of("Europe/Helsinki")));

        Map<String, TeacherCourse> coursesByRealisationIds = teacherCourses.stream()
              .collect(Collectors.toMap(c -> c.realisationId, Function.identity()));

        return teacherCourses
            .stream()
            .map(FunctionHelper.logAndIgnoreExceptions(c -> courseConverter.toDto(c, locale)))
            .filter(Objects::nonNull)
            .collect(toList());
    }

    public List<CourseDto> getStudentCourses(String studentNumber, Locale locale) {
        return studyRegistryService.getEnrollments(studentNumber).stream()
            .map(c -> courseConverter.toDto(c, locale))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(toList());
    }

    public List<String> getTeacherCourseIds(String teacherNumber) {
        return studyRegistryService.getTeacherCourses(teacherNumber, LocalDate.now()).stream()
            .filter(e -> !e.isCancelled)
            .map(e -> String.valueOf(e.realisationId))
            .collect(toList());
    }

    public List<String> getStudentCourseIds(String studentNumber) {
        return studyRegistryService.getEnrollments(studentNumber)
            .stream()
            .filter(e -> !e.isCancelled)
            .map(e -> String.valueOf(e.realisationId))
            .collect(toList());
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
