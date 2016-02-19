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

package fi.helsinki.opintoni.service.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.FavoriteDto;
import fi.helsinki.opintoni.dto.LinkFavoriteDto;
import fi.helsinki.opintoni.dto.TwitterFavoriteDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PortfolioFavoriteServiceTest extends SpringTest {

    @Autowired
    private PortfolioFavoriteService portfolioFavoriteService;

    @Test
    public void thatFavoritesAreFoundForPortfolio() {
        List<FavoriteDto> favoriteDtoList = portfolioFavoriteService.findByPortfolioId(3L);

        assertThat(favoriteDtoList).hasSize(3);
        assertThat(favoriteDtoList.get(0).getClass()).isEqualTo(LinkFavoriteDto.class);
        assertThat(favoriteDtoList.get(1).getClass()).isEqualTo(LinkFavoriteDto.class);
        assertThat(favoriteDtoList.get(2).getClass()).isEqualTo(TwitterFavoriteDto.class);
        assertThat(((LinkFavoriteDto)favoriteDtoList.get(0)).url).isEqualTo("http://www.helsinki.fi");
        assertThat(((LinkFavoriteDto)favoriteDtoList.get(1)).url).isEqualTo("http://www.iltalehti.fi");
    }
}
