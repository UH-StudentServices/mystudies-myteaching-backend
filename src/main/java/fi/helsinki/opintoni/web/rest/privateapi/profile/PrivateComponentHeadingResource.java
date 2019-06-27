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

package fi.helsinki.opintoni.web.rest.privateapi.profile;

import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.dto.profile.ComponentHeadingDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.profile.ComponentHeadingService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static fi.helsinki.opintoni.web.rest.RestConstants.MATCH_NUMBER;
import static fi.helsinki.opintoni.web.rest.RestConstants.PRIVATE_API_V1_PROFILE;

@RestController
@RequestMapping(
    value = PRIVATE_API_V1_PROFILE + "/{profileId:" + MATCH_NUMBER + "}/component-headings",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateComponentHeadingResource extends AbstractResource {

    private final ComponentHeadingService componentHeadingService;
    private final PermissionChecker permissionChecker;

    @Autowired
    public PrivateComponentHeadingResource(ComponentHeadingService componentHeadingService, PermissionChecker permissionChecker) {
        this.componentHeadingService = componentHeadingService;
        this.permissionChecker = permissionChecker;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ComponentHeadingDto> upsert(@UserId Long userId,
                                                      @PathVariable Long profileId,
                                                      @RequestBody ComponentHeadingDto requestDto) {
        permissionChecker.verifyPermission(userId, profileId, Profile.class);

        return response(componentHeadingService.upsert(profileId, requestDto));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{component}")
    public ResponseEntity delete(@UserId Long userId, @PathVariable Long profileId, @PathVariable ProfileComponent component) {
        permissionChecker.verifyPermission(userId, profileId, Profile.class);

        componentHeadingService.delete(profileId, component);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
