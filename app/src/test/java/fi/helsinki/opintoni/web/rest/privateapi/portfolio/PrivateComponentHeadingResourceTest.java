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
import fi.helsinki.opintoni.domain.portfolio.ComponentHeading;
import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.dto.portfolio.ComponentHeadingDto;
import fi.helsinki.opintoni.repository.portfolio.ComponentHeadingRepository;
import fi.helsinki.opintoni.service.portfolio.ComponentHeadingService;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ComponentHeadingService componentHeadingService;

    @Test
    public void thatComponentHeadingCanBeAdded() throws Exception {
        ComponentHeadingDto componentHeadingDto = new ComponentHeadingDto();
        componentHeadingDto.component = PortfolioComponent.ATTAINMENTS;
        componentHeadingDto.heading = "Third heading";

        // Adding new modified heading adds a new entry into headings list, there are already 2 headings,
        // defined by component_headings.csv

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH_SEGMENT)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(componentHeadingDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertThat(componentHeadingRepository.findByPortfolioId(STUDENT_PORTFOLIO_ID)).hasSize(3);
    }

    @Test
    public void thatComponentHeadingCanBeUpdated() throws Exception {
        ComponentHeadingDto componentHeadingDto = new ComponentHeadingDto();
        componentHeadingDto.component = PortfolioComponent.STUDIES;
        componentHeadingDto.heading = "Changed test heading";

        // There are already headings for STUDIES and DEGREES
        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH_SEGMENT)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(componentHeadingDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        List<ComponentHeading> componentHeadings = componentHeadingRepository.findByPortfolioId(STUDENT_PORTFOLIO_ID);
        assertThat(componentHeadings.stream().map(c -> c.heading).collect(Collectors.toList())).contains(componentHeadingDto.heading);
        assertThat(componentHeadings).hasSize(2);
    }

    @Test
    public void thatComponentHeadingIsDeleted() throws Exception {

        // Deleting a heading deletes only one element from the list
        // Remember that we have headings for STUDIES and DEGREES
        String componentPath = STUDENT_API_PATH_SEGMENT + "/" + PortfolioComponent.STUDIES.toString();

        mockMvc.perform(delete(RestConstants.PRIVATE_API_V1 + componentPath)
            .with(securityContext(studentSecurityContext())));

        assertThat(componentHeadingRepository.findByPortfolioId(STUDENT_PORTFOLIO_ID)).hasSize(1);
    }
}
