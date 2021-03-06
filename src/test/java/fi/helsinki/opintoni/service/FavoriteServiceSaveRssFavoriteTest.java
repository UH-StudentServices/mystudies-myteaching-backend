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

import fi.helsinki.opintoni.dto.RssFavoriteDto;
import fi.helsinki.opintoni.web.rest.privateapi.SaveRssFavoriteRequest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FavoriteServiceSaveRssFavoriteTest extends AbstractFavoriteServiceTest {

    private static final String URL1 = "http://bbc.co.uk/rss";

    private static final String URL2 = "http://www.dagetblader.se/rss";

    @Test
    public void rssFavoritesIsPersistedCorrectly() {
        SaveRssFavoriteRequest request = new SaveRssFavoriteRequest();
        request.url = URL1;
        RssFavoriteDto favorite = (RssFavoriteDto) favoriteService.saveRssFavorite(1L, request);
        assertThat(favorite.url).isEqualTo(URL1);
    }

    @Test
    public void firstRssFavoritesIsPersistedCorrectly() {
        SaveRssFavoriteRequest request = new SaveRssFavoriteRequest();
        request.url = URL2;
        RssFavoriteDto favorite = (RssFavoriteDto) favoriteService.saveRssFavorite(2L, request);
        assertThat(favorite.url).isEqualTo(URL2);
    }
}
