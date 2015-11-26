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
import fi.helsinki.opintoni.domain.portfolio.PortfolioVisibility;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PortfolioResourceTest extends SpringTest {

    private final String PORTFOLIO_API_URL = "/api/private/v1/portfolio";

    @Test
    public void thatNotFoundIsReturned() throws Exception {
        mockMvc.perform(get(PORTFOLIO_API_URL).with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatPortfolioIsCreated() throws Exception {
        mockMvc.perform(post(PORTFOLIO_API_URL)
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.url").value("https://opi-1.student.helsinki.fi/portfolio/#/olli.opettaja"));
    }

    @Test
    public void thatPortfolioIsFoundByPath() throws Exception {
        mockMvc.perform(get(PORTFOLIO_API_URL + "/find/" + "olli.opiskelija").with(securityContext
            (studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.backgroundUri").value(
                "https://opi-1.student.helsinki.fi/api/public/v1/images/backgrounds/file_3.jpg"
            ))
            .andExpect(jsonPath("$.componentVisibilities").isArray())
            .andExpect(jsonPath("$.componentVisibilities[0].component").value("WORK_EXPERIENCE"))
            .andExpect(jsonPath("$.componentVisibilities[0].visibility").value("PUBLIC"));
    }

    @Test
    public void thatPortfolioIsFoundById() throws Exception {
        mockMvc.perform(get(PORTFOLIO_API_URL + "/2").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void thatPortfolioIsUpdated() throws Exception {
        String updatedOwnerName = "Olli Opiskelija updated";
        String updatedIntro = "Introtext updated";
        PortfolioVisibility updatedVisibility = PortfolioVisibility.PUBLIC;

        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.ownerName = updatedOwnerName;
        portfolioDto.intro = updatedIntro;
        portfolioDto.visibility = updatedVisibility;

        mockMvc.perform(put(PORTFOLIO_API_URL + "/2").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(portfolioDto))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.ownerName").value(updatedOwnerName))
            .andExpect(jsonPath("$.intro").value(updatedIntro))
            .andExpect(jsonPath("$.visibility").value(updatedVisibility.name()));
    }

}
