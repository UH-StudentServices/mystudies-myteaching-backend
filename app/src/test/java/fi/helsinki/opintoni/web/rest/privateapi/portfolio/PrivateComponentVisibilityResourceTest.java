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
import fi.helsinki.opintoni.domain.portfolio.ComponentVisibility;
import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.repository.ComponentVisibilityRepository;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.componentvisibility.UpdateComponentVisibilityRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Optional;

import static fi.helsinki.opintoni.domain.portfolio.ComponentVisibility.Visibility;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateComponentVisibilityResourceTest extends SpringTest {

    @Autowired
    private ComponentVisibilityRepository componentVisibilityRepository;

    @Test
    public void thatComponentVisibilityIsCreated() throws Exception {
        componentVisibilityRepository.deleteAll();

        UpdateComponentVisibilityRequest request = createRequest(PortfolioComponent.ATTAINMENTS, Visibility.PRIVATE);

        mockMvc.perform(post("/api/private/v1/portfolio/2/componentvisibility")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        ComponentVisibility permission = getByIdAndComponent(2L, PortfolioComponent.ATTAINMENTS).get();

        assertThat(permission.visibility).isEqualTo(Visibility.PRIVATE);
        assertThat(permission.component).isEqualTo(PortfolioComponent.ATTAINMENTS);
    }

    @Test
    public void thatComponentVisibilityIsUpdated() throws Exception {
        assertThat(getByIdAndComponent(2L, PortfolioComponent.WORK_EXPERIENCE).isPresent()).isTrue();

        UpdateComponentVisibilityRequest request =
            createRequest(PortfolioComponent.WORK_EXPERIENCE, Visibility.PRIVATE);

        mockMvc.perform(post("/api/private/v1/portfolio/2/componentvisibility")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        ComponentVisibility permission = getByIdAndComponent(2L, PortfolioComponent.WORK_EXPERIENCE).get();

        assertThat(permission.visibility).isEqualTo(Visibility.PRIVATE);
        assertThat(permission.component).isEqualTo(PortfolioComponent.WORK_EXPERIENCE);
    }

    private Optional<ComponentVisibility> getByIdAndComponent(Long id, PortfolioComponent component) {
        return componentVisibilityRepository.findByPortfolioIdAndComponent(id, component);
    }

    private UpdateComponentVisibilityRequest createRequest(PortfolioComponent component, Visibility visibility) {
        UpdateComponentVisibilityRequest request = new UpdateComponentVisibilityRequest();
        request.component = component;
        request.visibility = visibility;
        return request;
    }

}
