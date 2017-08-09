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
import fi.helsinki.opintoni.dto.portfolio.ComponentHeadingDto;
import fi.helsinki.opintoni.dto.portfolio.ComponentOrderDto;
import fi.helsinki.opintoni.repository.portfolio.ComponentHeadingRepository;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.componentOrder.UpdateComponentOrderingRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateComponentHeadingResourceTest extends SpringTest {

    private static final String STUDENT_API_PATH_SEGMENT = "/portfolio/2/component-headings";

    private static final long STUDENT_PORTFOLIO_ID = 2L;

    @Autowired
    private ComponentHeadingRepository componentHeadingRepository;

    @Test
    public void thatComponentHeadingIsUpdated() throws Exception {
        ComponentHeadingDto componentHeadingDto = new ComponentHeadingDto();
        componentHeadingDto.component = PortfolioComponent.STUDIES;
        componentHeadingDto.heading = "Test heading";

        // Adding new modified heading adds a new entry into headings list

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH_SEGMENT)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(componentHeadingDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertThat(componentHeadingRepository.findByPortfolioId(STUDENT_PORTFOLIO_ID)).hasSize(1);

        // Adding a modified heading for component adds a new entry into headings list

        componentHeadingDto.component = PortfolioComponent.DEGREES;
        componentHeadingDto.heading = "Another test heading";

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH_SEGMENT)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(componentHeadingDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertThat(componentHeadingRepository.findByPortfolioId(STUDENT_PORTFOLIO_ID)).hasSize(2);

        // Changing heading for existing component heading doesn't create new entry into list

        componentHeadingDto.component = PortfolioComponent.DEGREES;
        componentHeadingDto.heading = "Changed heading";

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH_SEGMENT)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(componentHeadingDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertThat(componentHeadingRepository.findByPortfolioId(STUDENT_PORTFOLIO_ID)).hasSize(2);


    }

    @Test
    public void thatComponentHeadingIsDeleted() throws Exception {
        ComponentHeadingDto componentHeadingDto = new ComponentHeadingDto();
        componentHeadingDto.component = PortfolioComponent.STUDIES;
        componentHeadingDto.heading = "Test heading";

        // Have two edited headings

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH_SEGMENT)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(componentHeadingDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        componentHeadingDto.component = PortfolioComponent.DEGREES;
        componentHeadingDto.heading = "Another heading";

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH_SEGMENT)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(componentHeadingDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertThat(componentHeadingRepository.findByPortfolioId(STUDENT_PORTFOLIO_ID)).hasSize(2);

        // Deleting a heading deletes only one element from the list

        String componentPath = STUDENT_API_PATH_SEGMENT + "/" + PortfolioComponent.STUDIES.toString();

        mockMvc.perform(delete(RestConstants.PRIVATE_API_V1 + componentPath)
            .with(securityContext(studentSecurityContext())));

        assertThat(componentHeadingRepository.findByPortfolioId(STUDENT_PORTFOLIO_ID)).hasSize(1);

    }


}
