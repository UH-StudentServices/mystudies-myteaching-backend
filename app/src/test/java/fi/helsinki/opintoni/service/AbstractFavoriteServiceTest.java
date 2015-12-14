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
import fi.helsinki.opintoni.dto.*;
import fi.helsinki.opintoni.service.favorite.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

abstract class AbstractFavoriteServiceTest extends SpringTest {

    @Autowired
    protected FavoriteService favoriteService;

    protected final void assertTwitterFavorite(FavoriteDto dto, Long id, String feedType, String value) {
        assertFavorite(dto, id, "TWITTER");
        assertThat(dto, instanceOf(TwitterFavoriteDto.class));

        TwitterFavoriteDto twitterFavoriteDto = (TwitterFavoriteDto) dto;
        assertEquals(feedType, twitterFavoriteDto.feedType);
        assertEquals(value, twitterFavoriteDto.value);
    }

    protected final void assertRssFavorite(FavoriteDto dto, Long id, String url) {
        assertFavorite(dto, id, "RSS");
        assertThat(dto, instanceOf(RssFavoriteDto.class));

        RssFavoriteDto rssFavoriteDto = (RssFavoriteDto) dto;
        assertEquals(url, rssFavoriteDto.url);
    }

    protected final void assertUnisportFavorite(FavoriteDto dto, Long id, String url) {
        assertFavorite(dto, id, "UNISPORT");
        assertThat(dto, instanceOf(UnisportFavoriteDto.class));

        UnisportFavoriteDto unisportFavoriteDto = (UnisportFavoriteDto) dto;
        assertEquals(url, unisportFavoriteDto.url);
    }

    protected final void assertLinkFavorite(FavoriteDto dto, Long id, String url, String title) {
        assertFavorite(dto, id, "LINK");
        assertThat(dto, instanceOf(LinkFavoriteDto.class));

        LinkFavoriteDto linkFavoriteDto = (LinkFavoriteDto) dto;
        assertEquals(url, linkFavoriteDto.url);
        assertEquals(title, linkFavoriteDto.title);
    }

    private void assertFavorite(FavoriteDto dto, Long id, String type) {
        assertEquals(id, dto.id);
        assertEquals(type, dto.type);
    }
}
