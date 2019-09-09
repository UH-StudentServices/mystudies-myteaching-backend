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

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.FavoriteDto;
import fi.helsinki.opintoni.dto.RssFavoriteDto;
import fi.helsinki.opintoni.dto.UnisportFavoriteDto;
import fi.helsinki.opintoni.service.favorite.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

abstract class AbstractFavoriteServiceTest extends SpringTest {

    @Autowired
    protected FavoriteService favoriteService;

    protected final void assertRssFavorite(FavoriteDto dto, Long id, String url) {
        assertFavorite(dto, id, "RSS");
        assertThat(dto, instanceOf(RssFavoriteDto.class));

        RssFavoriteDto rssFavoriteDto = (RssFavoriteDto) dto;
        assertThat(rssFavoriteDto.url).isEqualTo(url);
    }

    protected final void assertUnisportFavorite(FavoriteDto dto, Long id) {
        assertFavorite(dto, id, "UNISPORT");
        assertThat(dto, instanceOf(UnisportFavoriteDto.class));
    }

    private void assertFavorite(FavoriteDto dto, Long id, String type) {
        assertThat(dto.id).isEqualTo(id);
        assertThat(dto.type).isEqualTo(type);
    }
}
