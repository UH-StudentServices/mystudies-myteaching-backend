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

package fi.helsinki.opintoni.service.converter;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.FacultyDto;
import fi.helsinki.opintoni.integration.studyregistry.StudyRight;
import fi.helsinki.opintoni.integration.studyregistry.oodi.OodiStudyRegistry;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.service.UserRoleService;
import fi.helsinki.opintoni.web.TestConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

public class FacultyConverterTest extends SpringTest {

    @MockBean
    UserRoleService mockUserRoleService;

    @MockBean
    OodiStudyRegistry mockOodiStudyRegistry;

    @Autowired
    private FacultyConverter facultyConverter;

    @Test
    public void thatOpenUniversityTeacherFacultyIsReturned() {
        when(mockUserRoleService.isOpenUniversityTeacher(anyString())).thenReturn(true);
        FacultyDto facultyDto = facultyConverter.getFacultyDto(createTeacher());
        assertThat(facultyDto.code).isEqualTo("A93000");
    }

    @Test
    public void thatOpenUniversityStudentFacultyIsReturned() {
        when(mockUserRoleService.isOpenUniversityStudent(anyString())).thenReturn(true);
        FacultyDto facultyDto = facultyConverter.getFacultyDto(createStudent());
        assertThat(facultyDto.code).isEqualTo("A93000");
    }

    @Test
    public void thatUnknownFacultyReturnsNull() {
        assertThat(facultyConverter.getFacultyDto(createStudent())).isNull();
    }

    @Test
    public void thatTeacherFacultyIsReturned() {
        FacultyDto facultyDto = facultyConverter.getFacultyDto(createTeacher());
        assertThat(facultyDto.code).isEqualTo("A90000");
    }

    @Test
    public void thatStudentFacultyIsReturned() {
        when(mockOodiStudyRegistry.getStudentStudyRights(anyString())).thenReturn(List.of(getStudentStudyRight("H70")));
        FacultyDto facultyDto = facultyConverter.getFacultyDto(createStudent());
        assertThat(facultyDto.code).isEqualTo("H70");
    }

    private StudyRight getStudentStudyRight(String faculty) {
        StudyRight sr = new StudyRight();
        sr.faculty = faculty;
        sr.priority = 1;
        return sr;
    }

    private AppUser createTeacher() {
        return new AppUser.AppUserBuilder()
            .personId("123")
            .employeeNumber(TestConstants.EMPLOYEE_NUMBER)
            .eduPersonPrincipalName("eduPersonPrincipalName")
            .teacherFacultyCode("A90000")
            .build();
    }

    private AppUser createStudent() {
        return new AppUser.AppUserBuilder()
            .personId("123")
            .studentNumber(TestConstants.STUDENT_NUMBER)
            .eduPersonPrincipalName("eduPersonPrincipalName")
            .build();
    }
}
