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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio.skillsandexpertise;

import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.dto.portfolio.SkillsAndExpertiseDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.portfolio.SkillsAndExpertiseService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static fi.helsinki.opintoni.web.rest.RestConstants.MATCH_NUMBER;
import static fi.helsinki.opintoni.web.rest.RestConstants.PRIVATE_API_V1;

@RestController
@RequestMapping(
    value = PRIVATE_API_V1 + "/portfolio/{portfolioId:" + MATCH_NUMBER + "}/skillsandexpertise",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateSkillsAndExpertiseResource extends AbstractResource {
    private final SkillsAndExpertiseService skillsAndExpertiseService;
    private final PermissionChecker permissionChecker;

    @Autowired
    public PrivateSkillsAndExpertiseResource(SkillsAndExpertiseService skillsAndExpertiseService,
                                             PermissionChecker permissionChecker) {
        this.skillsAndExpertiseService = skillsAndExpertiseService;
        this.permissionChecker = permissionChecker;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<SkillsAndExpertiseDto> update(@UserId Long userId,
        @PathVariable Long portfolioId,
        @RequestBody UpdateSkillsAndExpertiseRequest skillsAndExpertise) {
        permissionChecker.verifyPermission(userId, portfolioId, Portfolio.class);
        return response(skillsAndExpertiseService.updateSkillsAndExpertise(portfolioId, skillsAndExpertise));
    }
}

