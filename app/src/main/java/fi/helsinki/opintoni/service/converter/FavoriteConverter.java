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

package fi.helsinki.opintoni.service.converter;

import com.google.common.collect.ImmutableMap;
import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.domain.*;
import fi.helsinki.opintoni.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class FavoriteConverter {

    private final ImmutableMap<Class<?>, Function<Favorite, FavoriteDto>> converters;
    private final AppConfiguration appConfiguration;

    @Autowired
    public FavoriteConverter(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
        this.converters = ImmutableMap.of(
            TwitterFavorite.class, favorite -> toDto((TwitterFavorite) favorite),
            RssFavorite.class, favorite -> toDto((RssFavorite) favorite),
            LinkFavorite.class, favorite -> toDto((LinkFavorite) favorite),
            UnicafeFavorite.class, favorite -> toDto((UnicafeFavorite) favorite),
            UnisportFavorite.class, favorite -> toDto((UnisportFavorite) favorite)
        );
    }

    public FavoriteDto toDto(Favorite favorite) {
        return converters.get(favorite.getClass()).apply(favorite);
    }

    private TwitterFavoriteDto toDto(TwitterFavorite favorite) {
        return new TwitterFavoriteDto(
            favorite.id,
            favorite.type.name(),
            favorite.feedType.name(),
            favorite.value);
    }

    private RssFavoriteDto toDto(RssFavorite favorite) {
        return new RssFavoriteDto(
            favorite.id,
            favorite.type.name(),
            favorite.url,
            favorite.visibleItems);
    }

    private LinkFavoriteDto toDto(LinkFavorite favorite) {
        return new LinkFavoriteDto(
            favorite.id,
            favorite.type.name(),
            favorite.url,
            favorite.providerName,
            favorite.title,
            favorite.thumbnailUrl,
            favorite.thumbnailWidth,
            favorite.thumbnailHeight);
    }

    private UnisportFavoriteDto toDto(UnisportFavorite favorite) {
        return new UnisportFavoriteDto(
            favorite.id,
            favorite.type.name(),
            appConfiguration.get("unisportMyReservationsUrl")
        );
    }

    private UnicafeFavoriteDto toDto(UnicafeFavorite favorite) {
        return new UnicafeFavoriteDto(favorite.id, favorite.type.name(), favorite.restaurantId);
    }
}
