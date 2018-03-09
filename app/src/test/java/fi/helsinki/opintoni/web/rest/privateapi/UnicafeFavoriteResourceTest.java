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

package fi.helsinki.opintoni.web.rest.privateapi;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UnicafeFavoriteResourceTest extends SpringTest {

    private void insertUnicafeFavorite(Integer restaurantId, ResultMatcher expectedResult) throws Exception {
        UnicafeFavoriteRequest unicafeFavoriteRequest = new UnicafeFavoriteRequest();
        unicafeFavoriteRequest.restaurantId = restaurantId;

        mockMvc.perform(post("/api/private/v1/favorites/unicafe").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(unicafeFavoriteRequest)))
            .andExpect(expectedResult);
    }

    @Test
    public void thatUserCanAddUnicafeMenuFavorite() throws Exception {
        insertUnicafeFavorite(3, status().isOk());
    }

    @Test
    public void thatUserCanModifyUnicafeMenuFavorite() throws Exception {
        insertUnicafeFavorite(3, status().isOk());

        UnicafeFavoriteRequest unicafeFavoriteRequest = new UnicafeFavoriteRequest();
        unicafeFavoriteRequest.restaurantId = 4;

        mockMvc.perform(put("/api/private/v1/favorites/unicafe/11").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(unicafeFavoriteRequest)))
            .andExpect(status().isOk());
    }

}
