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

import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.domain.Favorite;
import fi.helsinki.opintoni.domain.LinkFavorite;
import fi.helsinki.opintoni.domain.RssFavorite;
import fi.helsinki.opintoni.domain.TwitterFavorite;
import fi.helsinki.opintoni.domain.UnicafeFavorite;
import fi.helsinki.opintoni.dto.FavoriteDto;
import fi.helsinki.opintoni.dto.LinkFavoriteDto;
import fi.helsinki.opintoni.dto.RssFavoriteDto;
import fi.helsinki.opintoni.dto.TwitterFavoriteDto;
import fi.helsinki.opintoni.dto.UnicafeFavoriteDto;
import fi.helsinki.opintoni.exception.http.UnprocessableEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FavoriteConverter {

    private final AppConfiguration appConfiguration;

    @Autowired
    public FavoriteConverter(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public FavoriteDto toDto(Favorite favorite) {
        switch (favorite.type) {
            case TWITTER:
                return toTwitterDto((TwitterFavorite) favorite);
            case RSS:
                return toRssDto((RssFavorite) favorite);
            case LINK:
                return toLinkDto((LinkFavorite) favorite);
            case UNICAFE:
                return toUnicafeDto((UnicafeFavorite) favorite);
            case FLAMMA_NEWS:
            case FLAMMA_EVENTS:
                return toFavoriteDto(favorite);
            default:
                throw new UnprocessableEntityException("No converter for type " + favorite.type.name());
        }
    }

    private TwitterFavoriteDto toTwitterDto(TwitterFavorite favorite) {
        return new TwitterFavoriteDto(
            favorite.id,
            favorite.type.name(),
            favorite.feedType.name(),
            favorite.value);
    }

    private RssFavoriteDto toRssDto(RssFavorite favorite) {
        return new RssFavoriteDto(
            favorite.id,
            favorite.type.name(),
            favorite.url);
    }

    private LinkFavoriteDto toLinkDto(LinkFavorite favorite) {
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

    private UnicafeFavoriteDto toUnicafeDto(UnicafeFavorite favorite) {
        return new UnicafeFavoriteDto(
            favorite.id,
            favorite.type.name(),
            favorite.restaurantId);
    }

    private FavoriteDto toFavoriteDto(Favorite favorite) {
        return new FavoriteDto(
            favorite.id,
            favorite.type.name());
    }
}

