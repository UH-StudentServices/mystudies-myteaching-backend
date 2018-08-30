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

package fi.helsinki.opintoni.service;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.LinkFavoriteDto;
import fi.helsinki.opintoni.sampledata.EmbedLySampleData;
import fi.helsinki.opintoni.service.favorite.FavoriteService;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import fi.helsinki.opintoni.web.rest.privateapi.InsertLinkFavoriteRequest;
import fi.helsinki.opintoni.web.rest.privateapi.OrderFavoritesRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FavoriteServiceSaveLinkFavoriteTest extends SpringTest {

    private static final Long USER_ID = 1L;
    private static final Long PORTFOLIO_ID = 5L;

    @Autowired
    private FavoriteService favoriteService;

    private InsertLinkFavoriteRequest insertRequest;

    @Before
    public void setup() {
        insertRequest = new InsertLinkFavoriteRequest();
        insertRequest.url = EmbedLySampleData.URL;
        insertRequest.providerName = EmbedLySampleData.PROVIDER_NAME;
        insertRequest.title = EmbedLySampleData.TITLE;
        insertRequest.thumbnailUrl = EmbedLySampleData.THUMBNAIL_URL;
        insertRequest.thumbnailHeight = EmbedLySampleData.THUMBNAIL_HEIGHT;
        insertRequest.thumbnailWidth = EmbedLySampleData.THUMBNAIL_WIDTH;
    }

    @Test
    public void linkFavoriteIsPersistedCorrectlyForPortfolio() throws Exception {
        LinkFavoriteDto favorite =
            (LinkFavoriteDto) favoriteService.insertLinkFavoriteForPortfolio(USER_ID, insertRequest, PORTFOLIO_ID);

        assertThat(favorite.url).isEqualTo(EmbedLySampleData.URL);
        assertThat(favorite.providerName).isEqualTo(EmbedLySampleData.PROVIDER_NAME);
        assertThat(favorite.title).isEqualTo(EmbedLySampleData.TITLE);
        assertThat(favorite.thumbnailUrl).isEqualTo(EmbedLySampleData.THUMBNAIL_URL);
        assertThat(favorite.thumbnailWidth).isEqualTo(EmbedLySampleData.THUMBNAIL_WIDTH);
        assertThat(favorite.thumbnailHeight).isEqualTo(EmbedLySampleData.THUMBNAIL_HEIGHT);
    }

    @Test
    public void thatFavoritesAreOrdered() throws Exception {
        OrderFavoritesRequest request = new OrderFavoritesRequest();
        request.favoriteIds = Lists.newArrayList(10L, 9L);

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + "/favorites/order")
            .with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[0].id").value(10))
            .andExpect(jsonPath("$.[1].id").value(9));
    }
}
