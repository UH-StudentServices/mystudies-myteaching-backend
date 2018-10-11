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
import fi.helsinki.opintoni.dto.portfolio.WorkExperienceDto;
import fi.helsinki.opintoni.service.portfolio.WorkExperienceService;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateWorkExperienceResourceTest extends SpringTest {

    private static final String RESOURCE_URL = "/api/private/v1/portfolio/2/workexperience";
    private static final long PORTFOLIO_ID = 2L;

    @Autowired
    private WorkExperienceService workExperienceService;

    @Test
    public void thatPortfolioWorkExperienceIsSaved() throws Exception {

        WorkExperienceDto workExperienceDto = new WorkExperienceDto();
        workExperienceDto.employer = "Helsingin Yliopisto";
        workExperienceDto.jobTitle = "Toimitusjohtaja";
        workExperienceDto.employerUrl = "http://www.helsinki.fi/";
        workExperienceDto.text =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
        workExperienceDto.startDate = LocalDate.of(2016, 6, 6);
        workExperienceDto.endDate = LocalDate.of(2016, 7, 6);

        WorkExperienceDto[] dtoArray = {workExperienceDto};

        mockMvc.perform(post(RESOURCE_URL).with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(dtoArray))
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].jobTitle").value(workExperienceDto.jobTitle))
            .andExpect(jsonPath("$[0].employer").value(workExperienceDto.employer))
            .andExpect(jsonPath("$[0].employerUrl").value(workExperienceDto.employerUrl))
            .andExpect(jsonPath("$[0].text").value(workExperienceDto.text))
            .andExpect(jsonPath("$[0].startDate[0]").value(workExperienceDto.startDate.getYear()))
            .andExpect(jsonPath("$[0].startDate[1]").value(workExperienceDto.startDate.getMonthValue()))
            .andExpect(jsonPath("$[0].startDate[2]").value(workExperienceDto.startDate.getDayOfMonth()))
            .andExpect(jsonPath("$[0].endDate[0]").value(workExperienceDto.endDate.getYear()))
            .andExpect(jsonPath("$[0].endDate[1]").value(workExperienceDto.endDate.getMonthValue()))
            .andExpect(jsonPath("$[0].endDate[2]").value(workExperienceDto.endDate.getDayOfMonth()));
    }
}
