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

import fi.helsinki.opintoni.integration.studyregistry.Enrollment;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryService;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserRoleServiceTest {

    private static final String STUDENT_NUMBER = "123";
    private static final String TEACHER_NUMBER = "321";
    private static final List<String> USER_ENROLLMENTS = newArrayList("123", "456");
    private static final List<String> USER_ENROLLMENTS_SOME_OPEN_UNIVERSITY = newArrayList("123", "A456");
    private static final List<String> USER_ENROLLMENTS_OPEN_UNIVERSITY = newArrayList("A123", "a456");

    private final StudyRegistryService studyRegistryService = mock(StudyRegistryService.class);
    private final UserRoleService userRoleService = new UserRoleService(studyRegistryService);

    private void setupMockDataForStudent(List<String> enrollments) {
        when(studyRegistryService.getEnrollments(STUDENT_NUMBER))
            .thenReturn(enrollments(enrollments));
    }

    private void setupMockDataForTeacher(List<String> enrollments) {
        when(studyRegistryService.getTeacherCourses(anyString(),  any(LocalDate.class)))
            .thenReturn(courses(enrollments));
    }

    @Test
    public void thatStudentIsOpenUniversityUserWhenAllCoursesAreOpenUniversityCourses() {
        setupMockDataForStudent(USER_ENROLLMENTS_OPEN_UNIVERSITY);
        assertThat(userRoleService.isOpenUniversityStudent(STUDENT_NUMBER)).isTrue();
    }

    @Test
    public void thatStudentIsNotOpenUniversityUserWhenSomeCoursesAreOpenUniversityCourses() {
        setupMockDataForStudent(USER_ENROLLMENTS_SOME_OPEN_UNIVERSITY);

        assertThat(userRoleService.isOpenUniversityStudent(STUDENT_NUMBER)).isFalse();
    }

    @Test
    public void thatStudentIsNotOpenUniversityUserWhenNoCoursesAreOpenUniversityCourses() {
        setupMockDataForStudent(USER_ENROLLMENTS);

        assertThat(userRoleService.isOpenUniversityStudent(STUDENT_NUMBER)).isFalse();
    }

    @Test
    public void thatStudentIsNotOpenUniversityUserWhenNoCoursesAreFound() {
        setupMockDataForStudent(newArrayList());

        assertThat(userRoleService.isOpenUniversityStudent(STUDENT_NUMBER)).isFalse();
    }

    @Test
    public void thatTeacherIsOpenUniversityUserWhenAllCoursesAreOpenUniversityCourses() {
        setupMockDataForTeacher(USER_ENROLLMENTS_OPEN_UNIVERSITY);
        assertThat(userRoleService.isOpenUniversityTeacher(TEACHER_NUMBER)).isTrue();
    }

    @Test
    public void thatTeacherIsNotOpenUniversityUserWhenSomeCoursesAreOpenUniversityCourses() {
        setupMockDataForTeacher(USER_ENROLLMENTS_SOME_OPEN_UNIVERSITY);

        assertThat(userRoleService.isOpenUniversityTeacher(TEACHER_NUMBER)).isFalse();
    }

    @Test
    public void thatTeacherIsNotOpenUniversityUserWhenNoCoursesAreOpenUniversityCourses() {
        setupMockDataForTeacher(USER_ENROLLMENTS);

        assertThat(userRoleService.isOpenUniversityTeacher(TEACHER_NUMBER)).isFalse();
    }

    @Test
    public void thatTeacherIsNotOpenUniversityUserWhenNoCoursesAreFound() {
        setupMockDataForTeacher(newArrayList());

        assertThat(userRoleService.isOpenUniversityTeacher(TEACHER_NUMBER)).isFalse();
    }

    private List<Enrollment> enrollments(List<String> learningOpportunityIds) {
        return learningOpportunityIds.stream()
            .map(learningOpportunityId -> {
                Enrollment enrollment = new Enrollment();
                enrollment.learningOpportunityId = learningOpportunityId;
                return enrollment;
            })
            .collect(Collectors.toList());
    }

    private List<TeacherCourse> courses(List<String> learningOpportunityIds) {
        return learningOpportunityIds.stream()
            .map(learningOpportunityId -> {
                TeacherCourse teacherCourse = new TeacherCourse();
                teacherCourse.learningOpportunityId = learningOpportunityId;
                return teacherCourse;
            })
            .collect(Collectors.toList());
    }
}
