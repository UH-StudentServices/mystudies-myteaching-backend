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
import fi.helsinki.opintoni.domain.Favorite;
import fi.helsinki.opintoni.domain.RssFavorite;
import fi.helsinki.opintoni.domain.TwitterFavorite;
import fi.helsinki.opintoni.domain.UnicafeFavorite;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.repository.FavoriteRepository;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.service.favorite.FavoriteService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public class FavoriteServiceCreateDefaultTest extends SpringTest {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Test
    public void thatDefaultFavoritesAreCreated() {
        User user = saveUser();

        favoriteService.createDefaultFavorites(user);

        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByOrderIndexAsc(user.id);

        assertThat(favorites).hasSize(8);
        assertThat(favorites.get(0), instanceOf(UnicafeFavorite.class));
        assertThat(favorites.get(1), instanceOf(TwitterFavorite.class));
        assertThat(favorites.get(2), instanceOf(RssFavorite.class));
        assertThat(favorites.get(3), instanceOf(RssFavorite.class));
        assertThat(favorites.get(4), instanceOf(RssFavorite.class));
        assertThat(favorites.get(5), instanceOf(TwitterFavorite.class));
        assertThat(favorites.get(6), instanceOf(RssFavorite.class));
        assertThat(favorites.get(7), instanceOf(RssFavorite.class));
    }

    private User saveUser() {
        User user = new User();
        user.personId = "personId";
        user.eduPersonPrincipalName = "eduPersonPrincipalName";
        user.accountStatus = User.AccountStatus.ACTIVE;
        user.lastLoginDate = DateTime.now();
        return userRepository.save(user);
    }
}
