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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FavoriteServiceOrderFavoritesTest extends AbstractFavoriteServiceTest {

    @Test
    public void favoritesAreOrderedCorrectly() {
        favoriteService.orderFavorites(1L, Arrays.asList(1L, 3L));

        final List<FavoriteDto> favorites = favoriteService.findByUserId(1L);
        assertEquals(new Long(1), favorites.get(0).id);
        assertEquals(new Long(3), favorites.get(1).id);
    }

    @Test
    public void favoritesAreOrderedCorrectlyForPortfolio() {
        favoriteService.orderPortfolioFavorites(1L, Arrays.asList(5L, 4L));

        final List<FavoriteDto> favorites = favoriteService.findByUserIdForPortfolio(1L);
        assertEquals(new Long(5), favorites.get(0).id);
        assertEquals(new Long(4), favorites.get(1).id);
    }

}
