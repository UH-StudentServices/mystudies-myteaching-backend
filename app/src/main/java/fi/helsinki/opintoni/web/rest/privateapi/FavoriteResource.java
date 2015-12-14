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

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.domain.Favorite;
import fi.helsinki.opintoni.domain.UnicafeFavorite;
import fi.helsinki.opintoni.dto.FavoriteDto;
import fi.helsinki.opintoni.dto.FeedDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.favorite.FavoriteService;
import fi.helsinki.opintoni.service.FeedService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1 + "/favorites",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class FavoriteResource extends AbstractResource {

    private final FavoriteService favoriteService;
    private final PermissionChecker permissionChecker;
    private final FeedService feedService;

    @Autowired
    public FavoriteResource(FavoriteService favoriteService,
                            PermissionChecker permissionChecker,
                            FeedService feedService) {
        this.favoriteService = favoriteService;
        this.permissionChecker = permissionChecker;
        this.feedService = feedService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<FavoriteDto>> getFavorites(@UserId Long userId) {
        return response(favoriteService.findByUserId(userId));
    }

    @RequestMapping(value = "/rss", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<FavoriteDto> saveRssFavorite(@UserId Long userId,
                                                       @RequestBody SaveRssFavoriteRequest request) {
        return response(favoriteService.saveRssFavorite(userId, request));
    }

    @RequestMapping(value = "/rss", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<FeedDto> getRssFeed(@RequestParam("url") String feedUrl, @RequestParam("limit") int limit) {
        return response(feedService.getFeed(feedUrl, limit));
    }

    @RequestMapping(value = "/unicafe", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<FavoriteDto> saveUnicafeFavorite(@UserId Long userId,
                                                           @RequestBody UnicafeFavoriteRequest request) {
        return response(favoriteService.insertUnicafeFavorite(userId, request.restaurantId));
    }

    @RequestMapping(
        value = "/unicafe/{unicafeFavoriteId}",
        method = RequestMethod.PUT,
        produces = WebConstants.APPLICATION_JSON_UTF8
    )
    @Timed
    public ResponseEntity<FavoriteDto> updateUnicafeFavorite(
        @UserId Long userId,
        @PathVariable("unicafeFavoriteId") Long unicafeFavoriteId,
        @RequestBody UnicafeFavoriteRequest request) {

        permissionChecker.verifyPermission(userId, unicafeFavoriteId, UnicafeFavorite.class);

        return response(favoriteService.updateUnicafeFavorite(request.restaurantId, unicafeFavoriteId));
    }

    @RequestMapping(value = "/twitter", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<FavoriteDto> saveTwitterFavorite(@UserId Long userId,
                                                           @RequestBody InsertTwitterFavoriteRequest request) {
        return response(favoriteService.insertTwitterFavorite(userId, request));
    }

    @RequestMapping(value = "/unisport", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<FavoriteDto> saveUnisportFavorite(@UserId Long userId) {
        return response(favoriteService.insertUnisportFavorite(userId));
    }

    @Timed
    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public ResponseEntity<List<FavoriteDto>> orderFavorites(@UserId Long userId,
                                                            @RequestBody OrderFavoritesRequest request) {

        permissionChecker.verifyPermission(userId, request.favoriteIds, Favorite.class);

        return response(() -> {
            favoriteService.orderFavorites(userId, request.favoriteIds);
            return favoriteService.findByUserId(userId);
        });
    }

    @RequestMapping(value = "/{favoriteId}", method = RequestMethod.DELETE)
    @Timed
    public ResponseEntity<List<FavoriteDto>> deleteFavorite(@UserId Long userId,
                                                            @PathVariable("favoriteId") Long favoriteId) {

        permissionChecker.verifyPermission(userId, favoriteId, Favorite.class);

        return response(() -> {
            favoriteService.deleteFavorite(favoriteId);
            return favoriteService.findByUserId(userId);
        });
    }
}
