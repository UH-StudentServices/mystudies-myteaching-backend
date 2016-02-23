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

package fi.helsinki.opintoni.web.rest.privateapi;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SessionResourceTest extends SpringTest {

    @Test
    public void getCurrentSessionReturnsCorrectResponse() throws Exception {
        defaultStudentRequestChain().enrollments().studyRights();

        mockMvc.perform(get("/api/private/v1/session").with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.username").value("opiskelija@helsinki.fi"))
            .andExpect(jsonPath("$.email").value("opiskelija@mail.helsinki.fi"))
            .andExpect(jsonPath("$.name").value("Olli Opiskelija"))
            .andExpect(jsonPath("$.roles", hasItem("STUDENT")))
            .andExpect(jsonPath("$.openUniversity").value(false))
            .andExpect(jsonPath("$.portfolioPath.student").value("olli.opiskelija"))
            .andExpect(jsonPath("$.faculty.code").value("H70"))
            .andExpect(jsonPath("$.faculty.uri").value("https://flamma.helsinki.fi/portal/units/valt"));
    }

}
