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

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FacultyConverterTest extends SpringTest {

    @Autowired
    private FacultyConverter facultyConverter;

    @Test
    public void thatOpenUniversityTeacherFacultyIsReturned() {
        defaultTeacherRequestChain().courses("teachercoursesopenuniversity.json");

        FacultyDto facultyDto = facultyConverter.getFacultyDto(createTeacher());

        assertEquals("A93000", facultyDto.code);
        assertEquals("https://flamma.helsinki.fi/portal/units/avoin", facultyDto.uri);
    }

    @Test
    public void thatOpenUniversityStudentFacultyIsReturned() {
        defaultStudentRequestChain().enrollments("enrollmentsopenuniversity.json");

        FacultyDto facultyDto = facultyConverter.getFacultyDto(createStudent());

        assertEquals("A93000", facultyDto.code);
        assertEquals("https://flamma.helsinki.fi/portal/units/avoin", facultyDto.uri);
    }

    @Test
    public void thatUnknownFacultyReturnsNull() {
        defaultStudentRequestChain().enrollments().studyRights("studentstudyrightswithunknownfaculty.json");

        assertNull(facultyConverter.getFacultyDto(createStudent()));
    }

    @Test
    public void thatTeacherFacultyIsReturned() {
        defaultTeacherRequestChain().courses();

        FacultyDto facultyDto = facultyConverter.getFacultyDto(createTeacher());

        assertEquals("A90000", facultyDto.code);
        assertEquals("https://flamma.helsinki.fi/portal/units/vetmed", facultyDto.uri);
    }

    @Test
    public void thatStudentFacultyIsReturned() {
        defaultStudentRequestChain().enrollments().studyRights();

        FacultyDto facultyDto = facultyConverter.getFacultyDto(createStudent());

        assertEquals("H70", facultyDto.code);
        assertEquals("https://flamma.helsinki.fi/portal/units/valt", facultyDto.uri);
    }

    private AppUser createTeacher() {
        return new AppUser.AppUserBuilder()
            .oodiPersonId("123")
            .teacherNumber(TestConstants.TEACHER_NUMBER)
            .eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation.FACULTY)
            .eduPersonAffiliations(Arrays.asList(SAMLEduPersonAffiliation.FACULTY))
            .eduPersonPrincipalName("eduPersonPrincipalName")
            .teacherFacultyCode("A90000")
            .build();
    }

    private AppUser createStudent() {
        return new AppUser.AppUserBuilder()
            .oodiPersonId("123")
            .studentNumber(TestConstants.STUDENT_NUMBER)
            .eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation.STUDENT)
            .eduPersonAffiliations(Arrays.asList(SAMLEduPersonAffiliation.STUDENT))
            .eduPersonPrincipalName("eduPersonPrincipalName")
            .build();
    }
}
