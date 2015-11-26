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

package fi.helsinki.opintoni.web.rest.publicapi.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.domain.portfolio.PortfolioVisibility;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicJobSearchResourcePermissionTest extends SpringTest {

    @Autowired
    private PortfolioRepository portfolioRepository;

    private void getWorkExperience(ResultMatcher expectedResult) throws Exception {
        mockMvc.perform(get("/api/public/v1/portfolio/2/jobsearch")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(expectedResult);
    }

    @Test
    public void thatUserCannotGetJobSearchFromPrivatePortfolioSheDoesNotOwnThroughPublicApi() throws Exception {
        getWorkExperience(status().isNotFound());
    }

    @Test
    public void thatUserCanGetJobSearchFromPublicPortfolioSheDoesNotOwnThroughPublicApi() throws Exception {
        Portfolio portfolio = portfolioRepository.findOne(2L);
        portfolio.visibility = PortfolioVisibility.PUBLIC;
        portfolioRepository.save(portfolio);

        getWorkExperience(status().isOk());
    }
}
