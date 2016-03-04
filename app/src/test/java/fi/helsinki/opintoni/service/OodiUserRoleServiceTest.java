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

import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiEnrollment;
import fi.helsinki.opintoni.integration.oodi.OodiTeacherCourse;
import fi.helsinki.opintoni.util.DateTimeUtil;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.google.common.collect.Lists.newArrayList;

public class OodiUserRoleServiceTest {

    private static final String STUDENT_NUMBER = "123";
    private static final String TEACHER_NUMBER = "321";
    private static final List<String> USER_ENROLLMENTS = newArrayList("123", "456");
    private static final List<String> USER_ENROLLMENTS_SOME_OPEN_UNIVERSITY = newArrayList("123", "A456");
    private static final List<String> USER_ENROLLMENTS_OPEN_UNIVERSITY = newArrayList("A123", "A456");

    private final OodiClient oodiClient = mock(OodiClient.class);
    private final OodiUserRoleService oodiUserRoleService = new OodiUserRoleService(oodiClient);

    private void setupOodiClientMockForStudent(List<String> enrollments) {
        when(oodiClient.getEnrollments(STUDENT_NUMBER))
            .thenReturn(enrollments(enrollments));
    }

    private void setupOodiClientMockForTeacher(List<String> enrollments) {
        when(oodiClient.getTeacherCourses(TEACHER_NUMBER, DateTimeUtil.getSemesterStartDateString(LocalDate.now())))
            .thenReturn(courses(enrollments));
    }

    @Test
    public void thatStudentIsOpenUniversityUserWhenAllCoursesAreOpenUniversityCourses() {
        setupOodiClientMockForStudent(USER_ENROLLMENTS_OPEN_UNIVERSITY);

        assertThat(oodiUserRoleService.isOpenUniversityStudent(STUDENT_NUMBER)).isTrue();
    }

    @Test
    public void thatStudentIsNotOpenUniversityUserWhenSomeCoursesAreOpenUniversityCourses() {
        setupOodiClientMockForStudent(USER_ENROLLMENTS_SOME_OPEN_UNIVERSITY);

        assertThat(oodiUserRoleService.isOpenUniversityStudent(STUDENT_NUMBER)).isFalse();
    }

    @Test
    public void thatStudentIsNotOpenUniversityUserWhenNoCoursesAreOpenUniversityCourses() {
        setupOodiClientMockForStudent(USER_ENROLLMENTS);

        assertThat(oodiUserRoleService.isOpenUniversityStudent(STUDENT_NUMBER)).isFalse();
    }

    @Test
    public void thatStudentIsNotOpenUniversityUserWhenNoCoursesAreFound() {
        setupOodiClientMockForStudent(newArrayList());

        assertThat(oodiUserRoleService.isOpenUniversityStudent(STUDENT_NUMBER)).isFalse();
    }

    @Test
    public void thatTeacherIsOpenUniversityUserWhenAllCoursesAreOpenUniversityCourses() {
        setupOodiClientMockForTeacher(USER_ENROLLMENTS_OPEN_UNIVERSITY);

        assertThat(oodiUserRoleService.isOpenUniversityTeacher(TEACHER_NUMBER)).isTrue();
    }

    @Test
    public void thatTeacherIsNotOpenUniversityUserWhenSomeCoursesAreOpenUniversityCourses() {
        setupOodiClientMockForTeacher(USER_ENROLLMENTS_SOME_OPEN_UNIVERSITY);

        assertThat(oodiUserRoleService.isOpenUniversityTeacher(TEACHER_NUMBER)).isFalse();
    }

    @Test
    public void thatTeacherIsNotOpenUniversityUserWhenNoCoursesAreOpenUniversityCourses() {
        setupOodiClientMockForTeacher(USER_ENROLLMENTS);

        assertThat(oodiUserRoleService.isOpenUniversityTeacher(TEACHER_NUMBER)).isFalse();
    }

    @Test
    public void thatTeacherIsNotOpenUniversityUserWhenNoCoursesAreFound() {
        setupOodiClientMockForTeacher(newArrayList());

        assertThat(oodiUserRoleService.isOpenUniversityTeacher(TEACHER_NUMBER)).isFalse();
    }

    private List<OodiEnrollment> enrollments(List<String> learningOpportunityIds) {
        return learningOpportunityIds.stream()
            .map(learningOpportunityId -> {
                OodiEnrollment oodiEnrollment = new OodiEnrollment();
                oodiEnrollment.learningOpportunityId = learningOpportunityId;
                return oodiEnrollment;
            })
            .collect(Collectors.toList());
    }

    private List<OodiTeacherCourse> courses(List<String> learningOpportunityIds) {
        return learningOpportunityIds.stream()
            .map(learningOpportunityId -> {
                OodiTeacherCourse oodiTeacherCourse = new OodiTeacherCourse();
                oodiTeacherCourse.basecode = learningOpportunityId;
                return oodiTeacherCourse;
            })
            .collect(Collectors.toList());
    }
}
