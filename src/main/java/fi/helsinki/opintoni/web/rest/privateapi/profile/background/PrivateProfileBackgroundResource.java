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

package fi.helsinki.opintoni.web.rest.privateapi.profile.background;

import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.dto.profile.ProfileBackgroundDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.profile.ProfileBackgroundService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.SelectBackgroundRequest;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.UploadImageBase64Request;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1_PROFILE + "/{profileId:" + RestConstants.MATCH_NUMBER + "}/background",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateProfileBackgroundResource extends AbstractResource {

    private final ProfileBackgroundService profileBackgroundService;
    private final PermissionChecker permissionChecker;

    public PrivateProfileBackgroundResource(ProfileBackgroundService profileBackgroundService,
                                            PermissionChecker permissionChecker) {
        this.profileBackgroundService = profileBackgroundService;
        this.permissionChecker = permissionChecker;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> uploadBackground(@UserId Long userId,
                                                    @PathVariable Long profileId,
                                                    @RequestBody UploadImageBase64Request request) {
        permissionChecker.hasPermission(userId, profileId, Profile.class);
        profileBackgroundService.uploadBackground(profileId, request);
        return response(true);
    }

    @RequestMapping(value = "/select", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> selectBackground(@UserId Long userId,
                                                    @PathVariable Long profileId,
                                                    @RequestBody SelectBackgroundRequest request) {
        permissionChecker.hasPermission(userId, profileId, Profile.class);
        profileBackgroundService.selectBackground(profileId, request);
        return response(true);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<ProfileBackgroundDto> getProfileBackgroundUri(@UserId Long userId,
                                                                        @PathVariable Long profileId) {
        permissionChecker.hasPermission(userId, profileId, Profile.class);
        String profileBackgroundUri = profileBackgroundService.getProfileBackgroundUri(profileId);
        return response(new ProfileBackgroundDto(profileBackgroundUri));
    }
}
