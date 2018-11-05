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
import fi.helsinki.opintoni.dto.portfolio.SampleDto;
import fi.helsinki.opintoni.service.portfolio.SampleService;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateSampleResourceTest extends SpringTest {

    private static final String RESOURCE_URL = "/api/private/v1/profile/2/samples";

    @Autowired
    private SampleService sampleService;

    @Test
    public void thatPortfolioSampleIsSaved() throws Exception {

        SampleDto sampleDto = new SampleDto();
        sampleDto.title = "Cool demo";
        sampleDto.description =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

        SampleDto[] dtoArray = {sampleDto};

        mockMvc.perform(post(RESOURCE_URL).with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(dtoArray))
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value(sampleDto.title))
            .andExpect(jsonPath("$[0].description").value(sampleDto.description));
    }
}
