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
import fi.helsinki.opintoni.web.rest.privateapi.InsertLinkFavoriteRequest;
import fi.helsinki.opintoni.web.rest.privateapi.InsertTwitterFavoriteRequest;
import fi.helsinki.opintoni.web.rest.privateapi.OrderFavoritesRequest;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivatePortfolioFavoriteResourceTest extends SpringTest {

    private static final String RESOURCE_URL = "/api/private/v1/portfolio/2/favorites";

    @Test
    public void thatPortfolioFavoritesAreReturned() throws Exception {
        mockMvc.perform(get(RESOURCE_URL)
            .with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[0].title").value("Iltalehti"))
            .andExpect(jsonPath("$.[1].title").value("Kauppalehti"));
    }

    @Test
    public void thatLinkFavoriteIsSaved() throws Exception {
        InsertLinkFavoriteRequest request = new InsertLinkFavoriteRequest();
        request.url = "https://iltalehti.fi";
        request.thumbnailUrl = "https://iltalehti.fi/thumbnail";
        request.title = "Iltalehti";

        mockMvc.perform(post(RESOURCE_URL + "/link")
            .with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://iltalehti.fi"))
            .andExpect(jsonPath("$.thumbnailUrl").value("https://iltalehti.fi/thumbnail"))
            .andExpect(jsonPath("$.title").value("Iltalehti"));
    }

    @Test
    public void thatTwitterFavoriteIsSaved() throws Exception {
        InsertTwitterFavoriteRequest request = new InsertTwitterFavoriteRequest();
        request.feedType = "USER_TIMELINE";
        request.value = "helsinkiuni";

        mockMvc.perform(post(RESOURCE_URL + "/twitter")
            .with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feedType").value("USER_TIMELINE"))
            .andExpect(jsonPath("$.value").value("helsinkiuni"));
    }

    @Test
    public void thatFavoritesAreOrdered() throws Exception {
        OrderFavoritesRequest request = new OrderFavoritesRequest();
        request.favoriteIds = Lists.newArrayList(8L, 6L);

        mockMvc.perform(post(RESOURCE_URL + "/order")
            .with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[0].id").value(8))
            .andExpect(jsonPath("$.[1].id").value(6));
    }

    @Test
    public void thatFavoriteIsDeleted() throws Exception {
        mockMvc.perform(delete(RESOURCE_URL + "/8")
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk());
    }
}
