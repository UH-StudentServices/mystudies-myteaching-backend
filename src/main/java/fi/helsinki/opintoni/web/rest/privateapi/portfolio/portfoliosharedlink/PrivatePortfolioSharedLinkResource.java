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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio.portfoliosharedlink;

import fi.helsinki.opintoni.dto.portfolio.PortfolioSharedLinkDto;
import fi.helsinki.opintoni.service.portfolio.PortfolioSharedLinkService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fi.helsinki.opintoni.web.rest.RestConstants.MATCH_NUMBER;
import static fi.helsinki.opintoni.web.rest.RestConstants.PRIVATE_API_V1_PORTFOLIO;

@RestController
@RequestMapping(
    value = PRIVATE_API_V1_PORTFOLIO + "/{portfolioId:" + MATCH_NUMBER + "}/sharedlinks",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivatePortfolioSharedLinkResource extends AbstractResource {

    private final PortfolioSharedLinkService portfolioSharedLinkService;

    @Autowired
    public PrivatePortfolioSharedLinkResource(PortfolioSharedLinkService portfolioSharedLinkService) {
        this.portfolioSharedLinkService = portfolioSharedLinkService;
    }

    @GetMapping
    public ResponseEntity<List<PortfolioSharedLinkDto>> getSharedLinks(@PathVariable Long portfolioId,
                                                                       @UserId Long userId) {
        return response(portfolioSharedLinkService.getSharedLinks(portfolioId, userId));
    }

    @PostMapping
    public ResponseEntity<PortfolioSharedLinkDto> createSharedLink(@PathVariable Long portfolioId,
                                                                   @RequestBody PortfolioSharedLinkDto sharedLinkDto,
                                                                   @UserId Long userId) {
        return response(portfolioSharedLinkService.createSharedLink(portfolioId, userId, sharedLinkDto));
    }

    @DeleteMapping(value = "/{sharedLinkId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteSharedLink(@PathVariable Long portfolioId,
                                 @PathVariable Long sharedLinkId,
                                 @UserId Long userId) {
        portfolioSharedLinkService.deleteSharedLink(portfolioId, userId, sharedLinkId);
    }
}
