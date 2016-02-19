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

import com.google.common.collect.Lists;
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

public class OodiUserRoleServiceTest {

    private final OodiClient oodiClient = mock(OodiClient.class);
    private final OodiUserRoleService oodiUserRoleService = new OodiUserRoleService(oodiClient);

    @Test
    public void thatStudentIsOpenUniversityUser() {
        when(oodiClient.getEnrollments("123"))
            .thenReturn(enrollments(Lists.newArrayList("A123", "A456")));

        assertThat(oodiUserRoleService.isOpenUniversityStudent("123")).isTrue();
    }

    @Test
    public void thatStudentIsNotOpenUniversityUser() {
        when(oodiClient.getEnrollments("123"))
            .thenReturn(enrollments(Lists.newArrayList("A123", "456")));

        assertThat(oodiUserRoleService.isOpenUniversityStudent("123")).isFalse();
    }

    @Test
    public void thatTeacherIsOpenUniversityUser() {
        when(oodiClient.getTeacherCourses("123", DateTimeUtil.getSemesterStartDateString(LocalDate.now())))
            .thenReturn(courses(Lists.newArrayList("A123", "A456")));

        assertThat(oodiUserRoleService.isOpenUniversityTeacher("123")).isTrue();
    }

    @Test
    public void thatTeacherIsNotOpenUniversityUser() {
        when(oodiClient.getTeacherCourses("123", DateTimeUtil.getSemesterStartDateString(LocalDate.now())))
            .thenReturn(courses(Lists.newArrayList("A123", "456")));

        assertThat(oodiUserRoleService.isOpenUniversityTeacher("123")).isFalse();
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
