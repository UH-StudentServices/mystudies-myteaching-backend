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

import fi.helsinki.opintoni.dto.FavoriteDto;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FavoriteServiceFindByUserIdTest extends AbstractFavoriteServiceTest {

    @Test
    public void usersFavoritesAreReturnedOrderedAndWithCorrectData() {
        List<FavoriteDto> favorites = favoriteService.findByUserId(1L);
        assertThat(favorites.size()).isEqualTo(6);

        assertRssFavorite(favorites.get(1), 3L, "http://www.mtv3.fi/rss");
        assertRssFavorite(favorites.get(4), 1L, "http://www.news.com/rss");
    }
}
