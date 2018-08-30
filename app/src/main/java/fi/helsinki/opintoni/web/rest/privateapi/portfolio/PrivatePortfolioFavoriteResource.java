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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.domain.Favorite;
import fi.helsinki.opintoni.dto.FavoriteDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.favorite.FavoriteService;
import fi.helsinki.opintoni.service.portfolio.PortfolioFavoriteService;
import fi.helsinki.opintoni.service.portfolio.PortfolioService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import fi.helsinki.opintoni.web.rest.privateapi.InsertLinkFavoriteRequest;
import fi.helsinki.opintoni.web.rest.privateapi.InsertTwitterFavoriteRequest;
import fi.helsinki.opintoni.web.rest.privateapi.OrderFavoritesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1 + "/portfolio/{portfolioId:" + RestConstants.MATCH_NUMBER + "}/favorites",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivatePortfolioFavoriteResource extends AbstractResource {

    private final PortfolioFavoriteService portfolioFavoriteService;
    private final FavoriteService favoriteService;
    private final PermissionChecker permissionChecker;

    @Autowired
    public PrivatePortfolioFavoriteResource(PortfolioFavoriteService portfolioFavoriteService,
                                            FavoriteService favoriteService,
                                            PermissionChecker permissionChecker,
                                            PortfolioService portfolioService) {
        this.portfolioFavoriteService = portfolioFavoriteService;
        this.favoriteService = favoriteService;
        this.permissionChecker = permissionChecker;
    }

    @RequestMapping(value = "/link", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<FavoriteDto> saveLinkFavorite(@UserId Long userId,
                                                        @RequestBody InsertLinkFavoriteRequest linkFavorite,
                                                        @PathVariable Long portfolioId) {
        return response(favoriteService.insertLinkFavoriteForPortfolio(userId, linkFavorite, portfolioId));
    }

    @RequestMapping(value = "/twitter", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<FavoriteDto> saveTwitterFavorite(@UserId Long userId,
                                                           @Valid @RequestBody InsertTwitterFavoriteRequest request,
                                                           @PathVariable Long portfolioId) {
        return response(favoriteService.insertTwitterFavoriteForPortfolio(userId, request, portfolioId));
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<List<FavoriteDto>> orderFavorites(@UserId Long userId,
                                                            @RequestBody OrderFavoritesRequest request,
                                                            @PathVariable Long portfolioId) {
        permissionChecker.verifyPermission(userId, request.favoriteIds, Favorite.class);

        return response(() -> {
            favoriteService.orderPortfolioFavorites(userId, request.favoriteIds);
            return favoriteService.findByPortfolioId(portfolioId);
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<FavoriteDto>> findByPortfolioId(@PathVariable Long portfolioId,
                                                               @UserId Long userId) {
        return response(portfolioFavoriteService.findByPortfolioId(portfolioId));
    }

    @RequestMapping(value = "/{favoriteId}", method = RequestMethod.DELETE)
    @Timed
    public ResponseEntity<List<FavoriteDto>> deleteFavorite(@UserId Long userId,
                                                            @PathVariable("favoriteId") Long favoriteId,
                                                            @PathVariable Long portfolioId) {
        permissionChecker.verifyPermission(userId, favoriteId, Favorite.class);

        return response(() -> {
            favoriteService.deleteFavorite(favoriteId);
            return favoriteService.findByPortfolioId(portfolioId);
        });
    }
}
