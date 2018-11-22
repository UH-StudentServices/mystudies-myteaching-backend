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

package fi.helsinki.opintoni.web.rest.privateapi.profile;

import fi.helsinki.opintoni.dto.profile.ProfileDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateProfileResourcePermissionTest extends AbstractProfileResourceTest {

    private static final String ANOTHER_USERS_PRIVATE_PROFILE_PATH = "/en/test-test";

    @Test
    public void thatAnotherUsersPrivateProfileIsNotFound() throws Exception {
        mockMvc.perform(get(STUDENT_PROFILE_API_PATH + ANOTHER_USERS_PRIVATE_PROFILE_PATH)
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatUserCannotUpdateProfileThatSheDoesNotOwn() throws Exception {
        mockMvc.perform(put(PRIVATE_PROFILE_API_PATH + "/1")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(new ProfileDto()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatStudentCannotCreateTeacherProfile() throws Exception {
        createTeacherProfile(studentSecurityContext())
            .andExpect(status().isForbidden());

        createTeacherProfile(studentSecurityContext(), Language.FI)
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatTeacherCannotCreateStudentProfile() throws Exception {
        createStudentProfile(teacherSecurityContext())
            .andExpect(status().isForbidden());

        createStudentProfile(teacherSecurityContext(), Language.FI)
            .andExpect(status().isForbidden());
    }
}
