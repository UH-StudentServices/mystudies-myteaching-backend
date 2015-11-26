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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio.degree;

import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.dto.portfolio.DegreeDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.portfolio.DegreeService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fi.helsinki.opintoni.web.rest.RestConstants.MATCH_NUMBER;
import static fi.helsinki.opintoni.web.rest.RestConstants.PRIVATE_API_V1;

@RestController
@RequestMapping(
    value = PRIVATE_API_V1 + "/portfolio/{portfolioId:" + MATCH_NUMBER + "}/degree",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateDegreeResource extends AbstractResource {

    private final DegreeService degreeService;
    private final PermissionChecker permissionChecker;

    @Autowired
    public PrivateDegreeResource(DegreeService degreeService,
                                 PermissionChecker permissionChecker) {
        this.degreeService = degreeService;
        this.permissionChecker = permissionChecker;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<DegreeDto>> findByPortfolioId(@UserId Long userId,
                                                             @PathVariable Long portfolioId) {
        permissionChecker.verifyPermission(userId, portfolioId, Portfolio.class);
        return response(degreeService.findByPortfolioId(portfolioId));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<List<DegreeDto>> update(@UserId Long userId,
                                                  @PathVariable Long portfolioId,
                                                  @RequestBody List<UpdateDegree> updateDegrees) {
        permissionChecker.verifyPermission(userId, portfolioId, Portfolio.class);
        return response(degreeService.updateDegrees(portfolioId, updateDegrees));
    }

}
