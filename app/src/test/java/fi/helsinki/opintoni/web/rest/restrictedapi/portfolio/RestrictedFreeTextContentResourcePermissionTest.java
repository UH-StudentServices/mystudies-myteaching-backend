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

package fi.helsinki.opintoni.web.rest.restrictedapi.portfolio;

import fi.helsinki.opintoni.domain.portfolio.ComponentVisibility;
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.domain.portfolio.PortfolioVisibility;
import fi.helsinki.opintoni.repository.ComponentVisibilityRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestrictedFreeTextContentResourcePermissionTest extends RestrictedPortfolioTest {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private ComponentVisibilityRepository componentVisibilityRepository;

    private static final String API_URL = "/api/restricted/v1/portfolio/2/freetextcontent";

    private void makePortfolioRestricted(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);
        portfolio.visibility = PortfolioVisibility.RESTRICTED;
        portfolioRepository.save(portfolio);
    }

    private void makeFreeTextContentPublic(Long portfolioId) {
        ComponentVisibility componentVisibility = new ComponentVisibility();
        componentVisibility.portfolio = portfolioRepository.findOne(portfolioId);
        componentVisibility.component = PortfolioComponent.FREE_TEXT_CONTENT;
        componentVisibility.visibility = ComponentVisibility.Visibility.PUBLIC;
        componentVisibilityRepository.save(componentVisibility);
    }

    private ResultActions getFreeTextContent(ResultMatcher expectedResult) throws Exception {
        return mockMvc.perform(get(API_URL)
            .with(securityContext(teacherSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(expectedResult);
    }

    @Test
    public void thatFreeTextContentIsRetrieved() throws Exception {
        makePortfolioRestricted(2L);
        makeFreeTextContentPublic(2L);
        getFreeTextContent(status().isOk());
    }

    @Test
    public void thatFreeTextContentCannotBeRetrievedFromRestrictedApiIfUserIsNotLoggedIn() throws Exception{
        makePortfolioRestricted(2L);
        makeFreeTextContentPublic(2L);
        mockMvc.perform(get(API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void thatFreeTextContentCannotBeRetrievedFromRestrictedApiIfItsNotPublic() throws Exception {
        makePortfolioRestricted(2L);
        getFreeTextContent(status().isForbidden());
    }

    @Test
    public void thatFreeTextContentCannotBeRetrievedFromRestrictedApiIfPortfolioIsNotPublic() throws Exception {
        getFreeTextContent(status().isForbidden());
    }
}
