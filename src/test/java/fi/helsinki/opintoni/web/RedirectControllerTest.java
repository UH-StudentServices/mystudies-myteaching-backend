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

package fi.helsinki.opintoni.web;

import fi.helsinki.opintoni.SpringTest;
import org.junit.Test;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RedirectControllerTest extends SpringTest {

    @Test
    public void thatStudentIsRedirected() throws Exception {
        mockMvc.perform(get("/redirect").with(securityContext(studentSecurityContext()))
            .param("state", "opintoni"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", "https://local.student.helsinki.fi:3000"));
    }

    @Test
    public void thatTeacherIsRedirected() throws Exception {
        mockMvc.perform(get("/redirect").with(securityContext(teacherSecurityContext()))
            .param("state", "opetukseni"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", "https://local.teacher.helsinki.fi:3000"));
    }

    @Test
    public void thatDefaultRedirectIsReturned() throws Exception {
        mockMvc.perform(get("/redirect").with(securityContext(studentSecurityContext()))
            .param("state", "notvalid"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", "/app"));
    }

}
