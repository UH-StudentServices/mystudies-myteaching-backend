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
import fi.helsinki.opintoni.dto.LearningOpportunityDto;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiTeacherCourse;
import fi.helsinki.opintoni.resolver.EventTypeResolver;
import fi.helsinki.opintoni.service.converter.CourseConverter;
import fi.helsinki.opintoni.service.converter.LearningOpportunityConverter;
import fi.helsinki.opintoni.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class CourseService {

    private final OodiClient oodiClient;
    private final CourseConverter courseConverter;
    private final LearningOpportunityConverter learningOpportunityConverter;
    private final EventTypeResolver eventTypeResolver;

    @Autowired
    public CourseService(OodiClient oodiClient,
                         CourseConverter courseConverter,
                         LearningOpportunityConverter learningOpportunityConverter,
                         EventTypeResolver eventTypeResolver) {
        this.oodiClient = oodiClient;
        this.courseConverter = courseConverter;
        this.learningOpportunityConverter = learningOpportunityConverter;
        this.eventTypeResolver = eventTypeResolver;
    }

    public List<CourseDto> getTeacherCourses(String teacherNumber, Locale locale) {
        List<OodiTeacherCourse> oodiTeacherCourses = oodiClient
            .getTeacherCourses(teacherNumber, DateTimeUtil.getSemesterStartDateString(LocalDate.now()));

          Map<String, OodiTeacherCourse> coursesByRealisationIds = oodiTeacherCourses.stream()
              .collect(Collectors.toMap(c -> c.realisationId, Function.identity()));

        return oodiTeacherCourses
            .stream()
            .map(c -> courseConverter.toDto(c, locale, isChildCourseWithoutRoot(c, coursesByRealisationIds)))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(toList());
    }

    public List<CourseDto> getStudentCourses(String studentNumber, Locale locale) {
        return oodiClient.getEnrollments(studentNumber).stream()
            .map(c -> courseConverter.toDto(c, locale))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(toList());
    }

    public List<String> getTeacherCourseIds(String teacherNumber) {
        return oodiClient.getTeacherCourses(teacherNumber, DateTimeUtil.getSemesterStartDateString(LocalDate.now())).stream()
            .filter(e -> !e.isCancelled)
            .map(e -> String.valueOf(e.realisationId))
            .collect(toList());
    }

    public List<String> getStudentCourseIds(String studentNumber) {
        return oodiClient.getEnrollments(studentNumber)
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

    public List<LearningOpportunityDto> getLearningOpportunities(List<String> learningOpportunityIds, Locale locale) {
        return learningOpportunityIds
            .stream()
            .map(oodiClient::getLearningOpportunity)
            .map(l -> learningOpportunityConverter.toDto(l, locale))
            .collect(toList());
    }

    private boolean isChildCourseWithoutRoot(OodiTeacherCourse oodiTeacherCourse, Map<String, OodiTeacherCourse> coursesByRealisationIds) {
        return !oodiTeacherCourse.realisationId.equals(oodiTeacherCourse.rootId) &&
            !coursesByRealisationIds.containsKey(oodiTeacherCourse.rootId);
    }

}
