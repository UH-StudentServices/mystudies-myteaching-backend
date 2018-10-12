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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
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

public class PrivatePortfolioResourcePermissionTest extends AbstractPortfolioResourceTest {

    private static final String ANOTHER_USERS_PRIVATE_PORTFOLIO_PATH = "/en/test-test";

    @Test
    public void thatAnotherUsersPrivatePortfolioIsNotFound() throws Exception {
        mockMvc.perform(get(STUDENT_PORTFOLIO_API_PATH + ANOTHER_USERS_PRIVATE_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatUserCannotUpdatePortfolioThatSheDoesNotOwn() throws Exception {
        mockMvc.perform(put(PRIVATE_PORTFOLIO_API_PATH + "/1")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(new PortfolioDto()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatStudentCannotCreateTeacherPortfolio() throws Exception {
        createTeacherPortfolio(studentSecurityContext())
            .andExpect(status().isForbidden());

        createTeacherPortfolio(studentSecurityContext(), Language.FI)
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatTeacherCannotCreateStudentPortfolio() throws Exception {
        createStudentPortfolio(teacherSecurityContext())
            .andExpect(status().isForbidden());

        createStudentPortfolio(teacherSecurityContext(), Language.FI)
            .andExpect(status().isForbidden());
    }
}
