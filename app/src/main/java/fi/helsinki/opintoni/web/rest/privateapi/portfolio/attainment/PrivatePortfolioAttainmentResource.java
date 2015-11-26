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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio.attainment;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.dto.StudyAttainmentDto;
import fi.helsinki.opintoni.dto.portfolio.StudyAttainmentWhitelistDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.StudyAttainmentService;
import fi.helsinki.opintoni.service.portfolio.PortfolioStudyAttainmentWhitelistService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1 + "/portfolio/{portfolioId:" + RestConstants.MATCH_NUMBER + "}/attainment",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivatePortfolioAttainmentResource extends AbstractResource {

    @Autowired
    private PortfolioStudyAttainmentWhitelistService whitelistService;

    @Autowired
    private PermissionChecker permissionChecker;

    @Autowired
    private StudyAttainmentService studyAttainmentService;

    @RequestMapping(method = RequestMethod.POST, value = "/whitelist")
    @Timed
    public ResponseEntity<StudyAttainmentWhitelistDto> update(@UserId Long userId,
                                                              @PathVariable Long portfolioId,
                                                              @Valid @RequestBody
                                                              StudyAttainmentWhitelistDto whitelistDto) {
        permissionChecker.verifyPermission(userId, portfolioId, Portfolio.class);
        return response(whitelistService.update(portfolioId, whitelistDto));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/whitelist")
    @Timed
    public ResponseEntity<StudyAttainmentWhitelistDto> getStudyAttainmentWhitelist(@UserId Long userId,
                                                                                   @PathVariable Long portfolioId) {
        permissionChecker.verifyPermission(userId, portfolioId, Portfolio.class);
        return response(whitelistService.get(portfolioId));
    }

    @RequestMapping(method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<StudyAttainmentDto>> getWhitelistedAttainmentsByPortfolioId(
        @PathVariable Long portfolioId,
        Locale locale) {
        return response(studyAttainmentService.getWhitelistedAttainmentsByPortfolioId(portfolioId, locale));
    }

}
