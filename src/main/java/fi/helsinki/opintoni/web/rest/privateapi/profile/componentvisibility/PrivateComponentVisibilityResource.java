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

package fi.helsinki.opintoni.web.rest.privateapi.profile.componentvisibility;

import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.profile.ComponentVisibilityService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static fi.helsinki.opintoni.web.rest.RestConstants.MATCH_NUMBER;
import static fi.helsinki.opintoni.web.rest.RestConstants.PRIVATE_API_V1_PROFILE;

@RestController
@RequestMapping(
    value = PRIVATE_API_V1_PROFILE + "/{profileId:" + MATCH_NUMBER + "}/componentvisibility",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateComponentVisibilityResource {

    private final PermissionChecker permissionChecker;
    private final ComponentVisibilityService componentVisibilityService;

    @Autowired
    public PrivateComponentVisibilityResource(PermissionChecker permissionChecker,
                                              ComponentVisibilityService componentVisibilityService) {
        this.permissionChecker = permissionChecker;
        this.componentVisibilityService = componentVisibilityService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> update(@UserId Long userId,
                                       @PathVariable Long profileId,
                                       @Valid @RequestBody UpdateComponentVisibilityRequest request) {
        permissionChecker.verifyPermission(userId, profileId, Profile.class);
        componentVisibilityService.update(profileId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
