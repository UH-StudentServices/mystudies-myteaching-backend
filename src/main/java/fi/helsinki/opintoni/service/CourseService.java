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
import fi.helsinki.opintoni.integration.coursecms.CourseCmsCourseUnitRealisation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryService;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.service.converter.CourseConverter;
import fi.helsinki.opintoni.util.CoursePageUtil;
import fi.helsinki.opintoni.util.FunctionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class CourseService {

    private final StudyRegistryService studyRegistryService;
    private final CourseConverter courseConverter;
    private final CoursePageUtil coursePageUtil;

    @Autowired
    public CourseService(StudyRegistryService studyRegistryService,
                         CourseConverter courseConverter, CoursePageUtil coursePageUtil) {
        this.studyRegistryService = studyRegistryService;
        this.courseConverter = courseConverter;
        this.coursePageUtil = coursePageUtil;
    }

    public List<CourseDto> getTeacherCourses(String teacherNumber, Locale locale) {
        List<TeacherCourse> teacherCourses = studyRegistryService
            .getTeacherCourses(teacherNumber, LocalDate.now(ZoneId.of("Europe/Helsinki")));

        Map<Boolean, List<TeacherCourse>> partitionedCourses = teacherCourses.stream()
            .collect(Collectors.groupingBy(FunctionHelper.logAndIgnoreExceptions(coursePageUtil::useNewCoursePageIntegration)));

        List<String> useOldCoursePages = Optional.ofNullable(partitionedCourses.get(false)).stream()
            .flatMap(List::stream)
            .map(cr -> cr.realisationId)
            .collect(Collectors.toList());

        Map<String, CoursePageCourseImplementation> coursePages = coursePageUtil.getOldCoursePages(useOldCoursePages, locale);

        List<String> useNewCoursePages = Optional.ofNullable(partitionedCourses.get(true)).stream()
            .flatMap(List::stream)
            .map(cr -> cr.realisationId)
            .collect(Collectors.toList());

        Map<String, CourseCmsCourseUnitRealisation> newCoursePages = coursePageUtil.getNewCoursePages(useNewCoursePages, locale);

        return teacherCourses
            .stream()
            .map(FunctionHelper.logAndIgnoreExceptions(
                c -> courseConverter.toDto(c, coursePages.get(c.realisationId), newCoursePages.get(c.realisationId), locale)))
            .filter(Objects::nonNull)
            .collect(toList());
    }
}
