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

import fi.helsinki.opintoni.domain.portfolio.ComponentVisibility;
import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.repository.ComponentVisibilityRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicPortfolioComponentVisibilityTest extends PublicPortfolioTest {

    @Autowired
    private ComponentVisibilityRepository componentVisibilityRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Before
    public void init() {
        setPrivateVisibilitiesForEveryComponent();
    }

    private void setPrivateVisibilitiesForEveryComponent() {
        componentVisibilityRepository.deleteAll();

        Arrays.asList(PortfolioComponent.values()).forEach(component -> {
            ComponentVisibility componentVisibility = new ComponentVisibility();
            componentVisibility.component = component;
            componentVisibility.visibility = ComponentVisibility.Visibility.PRIVATE;
            componentVisibility.portfolio = portfolioRepository.findOne(2L);
            componentVisibilityRepository.save(componentVisibility);
        });
    }

    @Test
    public void thatPrivateDegreesAreNotReturned() throws Exception {
        returnsForbidden(PUBLIC_PORTFOLIO_URL + "/degree");
    }

    @Test
    public void thatPrivateAttainmentsAreNotReturned() throws Exception {
        returnsForbidden(PUBLIC_PORTFOLIO_URL + "/attainment");
    }

    @Test
    public void thatPrivateContactInformationIsNotReturned() throws Exception {
        returnsForbidden(PUBLIC_PORTFOLIO_URL + "/contactinformation");
    }

    @Test
    public void thatPrivateKeywordsAreNotReturned() throws Exception {
        returnsForbidden(PUBLIC_PORTFOLIO_URL + "/keyword");
    }

    @Test
    public void thatPrivateWorkExperienceIsNotReturned() throws Exception {
        returnsForbidden(PUBLIC_PORTFOLIO_URL + "/workexperience");
    }

    @Test
    public void thatPrivateJobSearchIsNotReturned() throws Exception {
        returnsForbidden(PUBLIC_PORTFOLIO_URL + "/jobsearch");
    }

    @Test
    public void thatPrivateFavoritesAreNotReturned() throws Exception {
        returnsForbidden(PUBLIC_PORTFOLIO_URL + "/favorites");
    }

    @Test
    public void thatPrivateSummaryIsNotReturned() throws Exception {
        returnsForbidden(PUBLIC_PORTFOLIO_URL + "/summary");
    }

    @Test
    public void thatPrivateCreditsAreNotReturned() throws Exception {
        returnsForbidden(PUBLIC_PORTFOLIO_URL + "/credits");
    }

    private void returnsForbidden(String url) throws Exception {
        mockMvc.perform(get(url))
            .andExpect(status().isForbidden());
    }

}
