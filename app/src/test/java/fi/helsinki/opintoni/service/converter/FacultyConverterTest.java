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
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.security.enumerated.SAMLEduPersonAffiliation;
import fi.helsinki.opintoni.web.TestConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class FacultyConverterTest extends SpringTest {

    @Autowired
    private FacultyConverter facultyConverter;

    @Test
    public void thatOpenUniversityTeacherFacultyIsReturned() {
        defaultTeacherRequestChain().courses("teachercoursesopenuniversity.json");

        FacultyDto facultyDto = facultyConverter.getFacultyDto(createTeacher());

        assertThat(facultyDto.code).isEqualTo("A93000");
        assertThat(facultyDto.uri).isEqualTo("https://flamma.helsinki.fi/portal/units/avoin");
    }

    @Test
    public void thatOpenUniversityStudentFacultyIsReturned() {
        defaultStudentRequestChain().enrollments("enrollmentsopenuniversity.json");

        FacultyDto facultyDto = facultyConverter.getFacultyDto(createStudent());

        assertThat(facultyDto.code).isEqualTo("A93000");
        assertThat(facultyDto.uri).isEqualTo("https://flamma.helsinki.fi/portal/units/avoin");
    }

    @Test
    public void thatUnknownFacultyReturnsNull() {
        defaultStudentRequestChain().enrollments().studyRights("studentstudyrightswithunknownfaculty.json");

        assertThat(facultyConverter.getFacultyDto(createStudent())).isNull();
    }

    @Test
    public void thatTeacherFacultyIsReturned() {
        defaultTeacherRequestChain().courses();

        FacultyDto facultyDto = facultyConverter.getFacultyDto(createTeacher());

        assertThat(facultyDto.code).isEqualTo("A90000");
        assertThat(facultyDto.uri).isEqualTo("https://flamma.helsinki.fi/portal/units/vetmed");
    }

    @Test
    public void thatStudentFacultyIsReturned() {
        defaultStudentRequestChain().enrollments().studyRights();

        FacultyDto facultyDto = facultyConverter.getFacultyDto(createStudent());

        assertThat(facultyDto.code).isEqualTo("H70");
        assertThat(facultyDto.uri).isEqualTo("https://flamma.helsinki.fi/portal/units/valt");
    }

    private AppUser createTeacher() {
        return new AppUser.AppUserBuilder()
            .oodiPersonId("123")
            .employeeNumber(TestConstants.EMPLOYEE_NUMBER)
            .eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation.FACULTY)
            .eduPersonAffiliations(singletonList(SAMLEduPersonAffiliation.FACULTY))
            .eduPersonPrincipalName("eduPersonPrincipalName")
            .teacherFacultyCode("A90000")
            .build();
    }

    private AppUser createStudent() {
        return new AppUser.AppUserBuilder()
            .oodiPersonId("123")
            .studentNumber(TestConstants.STUDENT_NUMBER)
            .eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation.STUDENT)
            .eduPersonAffiliations(singletonList(SAMLEduPersonAffiliation.STUDENT))
            .eduPersonPrincipalName("eduPersonPrincipalName")
            .build();
    }
}
