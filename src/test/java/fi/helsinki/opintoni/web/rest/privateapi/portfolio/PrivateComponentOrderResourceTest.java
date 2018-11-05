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

import com.google.common.collect.ImmutableList;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.dto.portfolio.ComponentOrderDto;
import fi.helsinki.opintoni.repository.portfolio.ComponentOrderRepository;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.componentorder.UpdateComponentOrderingRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateComponentOrderResourceTest extends SpringTest {

    private static final String STUDENT_API_PATH_SEGMENT = "/profile/2/component-orders";

    private static final long STUDENT_PORTFOLIO_ID = 2L;

    @Autowired
    private ComponentOrderRepository componentOrderRepository;

    @Test
    public void thatComponentOrderIsUpdated() throws Exception {
        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH_SEGMENT)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(updateRequest()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertThat(componentOrderRepository.findByPortfolioId(STUDENT_PORTFOLIO_ID))
            .hasSize(4)
            .extracting("component", "orderValue").contains(
                tuple(PortfolioComponent.LANGUAGE_PROFICIENCIES, 1),
                tuple(PortfolioComponent.WORK_EXPERIENCE, 2),
                tuple(PortfolioComponent.ATTAINMENTS, 3),
                tuple(PortfolioComponent.STUDIES, 4)
            );
    }

    private UpdateComponentOrderingRequest updateRequest() {
        UpdateComponentOrderingRequest request = new UpdateComponentOrderingRequest();

        request.componentOrders = ImmutableList.of(
            componentOrderDto(PortfolioComponent.LANGUAGE_PROFICIENCIES, 1),
            componentOrderDto(PortfolioComponent.WORK_EXPERIENCE, 2),
            componentOrderDto(PortfolioComponent.ATTAINMENTS, 3),
            componentOrderDto(PortfolioComponent.STUDIES, 4)
        );

        return request;
    }

    private ComponentOrderDto componentOrderDto(PortfolioComponent component, int orderValue) {
        ComponentOrderDto dto = new ComponentOrderDto();

        dto.component = component;
        dto.orderValue = orderValue;

        return dto;
    }
}
