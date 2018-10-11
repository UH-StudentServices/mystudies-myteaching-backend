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
import fi.helsinki.opintoni.dto.portfolio.JobSearchDto;
import fi.helsinki.opintoni.service.portfolio.JobSearchService;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateJobSearchResourceTest extends SpringTest {

    private static final String RESOURCE_URL = "/api/private/v1/portfolio/2/jobsearch";
    private static final String CONTACT_EMAIL = "olli.opiskelija@helsinki.fi";
    private static final String HEADLINE = "Haen kesätöitä";
    private static final String TEXT = "Lorem ipsum";
    private static final long PORTFOLIO_ID = 2L;

    @Autowired
    private JobSearchService jobSearchService;

    private void saveJobSearch(ResultMatcher expectedResult) throws Exception {
        JobSearchDto jobSearchDto = new JobSearchDto();
        jobSearchDto.contactEmail = CONTACT_EMAIL;
        jobSearchDto.headline = HEADLINE;
        jobSearchDto.text = TEXT;

        mockMvc.perform(post(RESOURCE_URL).with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(jobSearchDto))
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("contactEmail").value(jobSearchDto.contactEmail))
            .andExpect(jsonPath("headline").value(jobSearchDto.headline))
            .andExpect(jsonPath("text").value(jobSearchDto.text))
            .andExpect(expectedResult);
    }

    private void deleteJobSearch() throws Exception {
        mockMvc.perform(delete(RESOURCE_URL).with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void thatJobSearchGetsSaved() throws Exception {
        deleteJobSearch();
        saveJobSearch(status().isOk());

        assertThat(jobSearchService.findByPortfolioId(PORTFOLIO_ID).contactEmail).isEqualTo(CONTACT_EMAIL);
    }

    @Test
    public void thatJobSearchIsDeleted() throws Exception {
        deleteJobSearch();

        assertThat(jobSearchService.findByPortfolioId(PORTFOLIO_ID)).isNull();
    }

}
