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

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FavoriteResourcePermissionsTest extends SpringTest {

    @Test
    public void thatUserCannotDeleteFavoriteSheDoesNotOwn() throws Exception {
        mockMvc.perform(delete("/api/private/v1/favorites/1").with(securityContext(studentSecurityContext())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatUserCannotUpdateUnicafeFavoriteSheDoesNotOwn() throws Exception {

        UnicafeFavoriteRequest unicafeFavoriteRequest = new UnicafeFavoriteRequest();
        unicafeFavoriteRequest.restaurantId = 3;

        mockMvc.perform(put("/api/private/v1/favorites/unicafe/7").with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(unicafeFavoriteRequest)))
            .andExpect(status().isForbidden());
    }

}
