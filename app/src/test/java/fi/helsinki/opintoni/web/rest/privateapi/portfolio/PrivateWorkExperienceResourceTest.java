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
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateWorkExperienceResourceTest extends SpringTest {

    private static final String RESOURCE_URL = "/api/private/v1/portfolio/2/workexperience";

    @Test
    public void thatPortfolioWorkExperienceIsReturned() throws Exception {
        mockMvc.perform(get(RESOURCE_URL).with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].jobTitle").value("Harjoittelija"))
            .andExpect(jsonPath("$.[0].employer").value("HY"))
            .andExpect(jsonPath("$.[0].startDate[0]").value(2015))
            .andExpect(jsonPath("$.[0].startDate[1]").value(6))
            .andExpect(jsonPath("$.[0].startDate[2]").value(6));
    }

    @Test
    public void thatPortfolioWorkExperienceIsSaved() throws Exception {

        WorkExperienceDto workExperienceDto = new WorkExperienceDto();
        workExperienceDto.employer = "Helsingin Yliopisto";
        workExperienceDto.jobTitle = "Toimitusjohtaja";
        workExperienceDto.startDate = LocalDate.of(2016, 6, 6);
        workExperienceDto.endDate = LocalDate.of(2016, 7, 6);

        mockMvc.perform(post(RESOURCE_URL).with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(workExperienceDto))
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jobTitle").value(workExperienceDto.jobTitle))
            .andExpect(jsonPath("$.employer").value(workExperienceDto.employer))
            .andExpect(jsonPath("$.startDate[0]").value(workExperienceDto.startDate.getYear()))
            .andExpect(jsonPath("$.startDate[1]").value(workExperienceDto.startDate.getMonthValue()))
            .andExpect(jsonPath("$.startDate[2]").value(workExperienceDto.startDate.getDayOfMonth()))
            .andExpect(jsonPath("$.endDate[0]").value(workExperienceDto.endDate.getYear()))
            .andExpect(jsonPath("$.endDate[1]").value(workExperienceDto.endDate.getMonthValue()))
            .andExpect(jsonPath("$.endDate[2]").value(workExperienceDto.endDate.getDayOfMonth()));
    }

    @Test
    public void thatPortfolioWorkExperienceIsDeleted() throws Exception {
        mockMvc.perform(delete(RESOURCE_URL + "/1").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mockMvc.perform(get(RESOURCE_URL).with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(0)));
    }
}
