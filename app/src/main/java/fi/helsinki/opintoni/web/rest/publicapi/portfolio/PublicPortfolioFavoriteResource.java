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

package fi.helsinki.opintoni.web.rest.publicapi.portfolio;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.dto.FavoriteDto;
import fi.helsinki.opintoni.security.authorization.portfolio.PublicVisibility;
import fi.helsinki.opintoni.service.portfolio.PortfolioFavoriteService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(
    value = RestConstants.PUBLIC_API_V1 + "/portfolio/{portfolioId:" + RestConstants.MATCH_NUMBER + "}/favorites",
    produces = WebConstants.APPLICATION_JSON_UTF8)
@PublicVisibility(PortfolioComponent.FAVORITES)
public class PublicPortfolioFavoriteResource extends AbstractResource {

    private final PortfolioFavoriteService portfolioFavoriteService;

    @Autowired
    public PublicPortfolioFavoriteResource(PortfolioFavoriteService portfolioFavoriteService) {
        this.portfolioFavoriteService = portfolioFavoriteService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<FavoriteDto>> findByPortfolioId(@PathVariable Long portfolioId) {
        return response(portfolioFavoriteService.findByPortfolioId(portfolioId));
    }
}
