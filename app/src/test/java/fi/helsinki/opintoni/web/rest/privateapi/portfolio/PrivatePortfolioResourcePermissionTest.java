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

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static fi.helsinki.opintoni.web.rest.RestConstants.PRIVATE_API_V1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivatePortfolioResourcePermissionTest extends SpringTest {

    private static final String PORTFOLIO_PATH = PRIVATE_API_V1 + "/portfolio";
    private static final String STUDENT_PORTFOLIO_PATH = PRIVATE_API_V1 + "/portfolio/student";
    private static final String TEACHER_PORTFOLIO_PATH = PRIVATE_API_V1 + "/portfolio/teacher";
    private static final String STUDENT_PORTFOLIO_USERNAME= "olli.opiskelija";
    private static final String PORTFOLIO_ID = "1";

    @Test
    public void thatUserCannotLoadPortfolioFromPrivateApiThatSheDoesNotOwn() throws Exception {
        mockMvc.perform(get(STUDENT_PORTFOLIO_PATH + "/" + STUDENT_PORTFOLIO_USERNAME)
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatUserCannotUpdatePortfolioThatSheDoesNotOwn() throws Exception {
        mockMvc.perform(put(PORTFOLIO_PATH + "/" + PORTFOLIO_ID).with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(new PortfolioDto()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatStudentCannotCreateTeacherPortfolio() throws Exception {
        mockMvc.perform(post(TEACHER_PORTFOLIO_PATH)
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatTeacherCannotCreateStudentPortfolio() throws Exception {
        mockMvc.perform(post(STUDENT_PORTFOLIO_PATH)
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }
}
