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
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.keyword.Keyword;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.keyword.UpdateKeywordsRequest;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class PrivatePortfolioKeywordRelationshipResourceTest extends SpringTest {

    private static final String RESOURCE_URL = "/api/private/v1/portfolio/2/keyword";

    @Test
    public void thatKeywordsAreUpdated() throws Exception {
        Keyword keyword = new Keyword();
        keyword.title = "New keyword";

        UpdateKeywordsRequest request = new UpdateKeywordsRequest();
        request.keywords = Lists.newArrayList(keyword);

        mockMvc.perform(post(RESOURCE_URL)
            .with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request)))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title").value("New keyword"));
    }

}
