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

package fi.helsinki.opintoni.resolver;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.web.TestConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class UserFacultyResolverTest extends SpringTest {

    @Autowired
    private UserFacultyResolver userFacultyResolver;

    @Test
    public void thatStudentFacultyIsResolved() {
        configureStudentSecurityContext();
        defaultStudentRequestChain().enrollments().studyRights();

        String faculty = userFacultyResolver.getStudentFacultyCode(TestConstants.STUDENT_NUMBER);

        assertThat(faculty).isEqualTo("H70");
    }

    @Test
    public void thatDefaultStudentFacultyIsResolved() {
        configureStudentSecurityContext();
        defaultStudentRequestChain().enrollments().studyRights("studentstudyrightswithnopriorityfaculty.json");

        String faculty = userFacultyResolver.getStudentFacultyCode(TestConstants.STUDENT_NUMBER);

        assertThat(faculty).isEqualTo("default");
    }

    @Test
    public void thatOpenUniversityFacultyIsReturned() {
        configureStudentSecurityContext();
        defaultStudentRequestChain().enrollments("enrollmentsopenuniversity.json");

        String faculty = userFacultyResolver.getStudentFacultyCode(TestConstants.STUDENT_NUMBER);

        assertThat(faculty).isEqualTo("A93000");
    }

}
