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

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.integration.studyregistry.Enrollment;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryService;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.oodi.OodiEnrollment;
import fi.helsinki.opintoni.integration.studyregistry.oodi.OodiTeacherCourse;
import fi.helsinki.opintoni.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserRoleService {

    private final StudyRegistryService studyRegistryService;

    @Autowired
    public UserRoleService(StudyRegistryService studyRegistryService) {
        this.studyRegistryService = studyRegistryService;
    }

    @Cacheable(value = CacheConstants.IS_OPEN_UNIVERSITY_TEACHER, cacheManager = "transientCacheManager")
    public boolean isOpenUniversityTeacher(String teacherNumber) {
        List<TeacherCourse> teacherCourses =
            studyRegistryService.getTeacherCourses(teacherNumber, DateTimeUtil.getSemesterStartDateString(LocalDate.now()));

        return !teacherCourses.isEmpty() &&
            teacherCourses
                .stream()
                .map(course -> course.learningOpportunityId)
                .allMatch(this::isOpenUniversityId);
    }

    @Cacheable(value = CacheConstants.IS_OPEN_UNIVERSITY_STUDENT, cacheManager = "transientCacheManager")
    public boolean isOpenUniversityStudent(String studentNumber) {
        List<Enrollment> enrollments = studyRegistryService.getEnrollments(studentNumber);

        return !enrollments.isEmpty() &&
            enrollments
                .stream()
                .map(enrollment -> enrollment.learningOpportunityId)
                .allMatch(this::isOpenUniversityId);
    }

    private boolean isOpenUniversityId(String code) {
        return code.matches("^(A|a).*");
    }
}
