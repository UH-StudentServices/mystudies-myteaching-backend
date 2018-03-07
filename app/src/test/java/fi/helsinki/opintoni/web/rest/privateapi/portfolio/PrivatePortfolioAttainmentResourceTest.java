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


import com.google.common.collect.Lists;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.portfolio.StudyAttainmentWhitelistDto;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivatePortfolioAttainmentResourceTest extends SpringTest {

    private static final String RESOURCE_URL = "/api/private/v1/portfolio/2/attainment";

    @Test
    public void thatAttainmentsAreReturned() throws Exception {
        defaultStudentRequestChain().roles().attainments();

        mockMvc.perform(get(RESOURCE_URL)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").value(hasSize(2)))
            .andExpect(jsonPath("$[0].studyAttainmentId").value(1))
            .andExpect(jsonPath("$[1].studyAttainmentId").value(2));
    }

    @Test
    public void thatWhitelistedAttainmentIdsAreReturned() throws Exception {
        mockMvc.perform(get(RESOURCE_URL + "/whitelist")
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.oodiStudyAttainmentIds").isArray())
            .andExpect(jsonPath("$.oodiStudyAttainmentIds").value(hasSize(2)))
            .andExpect(jsonPath("$.oodiStudyAttainmentIds[0]").value(1))
            .andExpect(jsonPath("$.oodiStudyAttainmentIds[1]").value(2));
    }

    @Test
    public void thatWhitelistedAttainmentIdsAreUpdated() throws Exception {
        StudyAttainmentWhitelistDto dto = new StudyAttainmentWhitelistDto();
        dto.oodiStudyAttainmentIds = Lists.newArrayList(3L, 4L);

        mockMvc.perform(post(RESOURCE_URL + "/whitelist")
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(dto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.oodiStudyAttainmentIds").isArray())
            .andExpect(jsonPath("$.oodiStudyAttainmentIds").value(hasSize(2)))
            .andExpect(jsonPath("$.oodiStudyAttainmentIds[0]").value(3))
            .andExpect(jsonPath("$.oodiStudyAttainmentIds[1]").value(4));
    }
}
