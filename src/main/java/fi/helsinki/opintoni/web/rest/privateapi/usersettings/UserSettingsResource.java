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

package fi.helsinki.opintoni.web.rest.privateapi.usersettings;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.domain.UserSettings;
import fi.helsinki.opintoni.dto.UserSettingsDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.UserSettingsService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1 + "/usersettings",
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class UserSettingsResource extends AbstractResource {

    private final UserSettingsService userSettingsService;
    private final PermissionChecker permissionChecker;

    @Autowired
    public UserSettingsResource(UserSettingsService userSettingsService, PermissionChecker permissionChecker) {
        this.userSettingsService = userSettingsService;
        this.permissionChecker = permissionChecker;
    }

    @GetMapping
    @Timed
    public ResponseEntity<UserSettingsDto> get(@UserId Long userId) {
        return response(userSettingsService.findByUserId(userId));
    }

    @PutMapping(value = "/{id:" + RestConstants.MATCH_NUMBER + "}")
    @Timed
    public ResponseEntity<UserSettingsDto> update(@UserId Long userId,
                                                  @PathVariable Long id,
                                                  @RequestBody UpdateUserSettingsRequest request) {
        permissionChecker.verifyPermission(userId, id, UserSettings.class);
        return response(userSettingsService.update(id, request));
    }

    @PutMapping(value = "/{id}/avatar")
    @Timed
    public ResponseEntity<Boolean> uploadUserAvatar(@UserId Long userId,
                                                    @PathVariable Long id,
                                                    @RequestBody UploadImageBase64Request request) {
        permissionChecker.verifyPermission(userId, id, UserSettings.class);
        userSettingsService.updateUserAvatar(id, request.imageBase64);
        return response(true);
    }

    @PutMapping(value = "/{id}/background")
    @Timed
    public ResponseEntity<UserSettingsDto> uploadBackground(@UserId Long userId,
                                                            @PathVariable Long id,
                                                            @RequestBody UploadImageBase64Request request) {
        permissionChecker.verifyPermission(userId, id, UserSettings.class);
        return response(userSettingsService.updateBackground(id, request));
    }

    @PutMapping(value = "/{id}/selectbackground")
    @Timed
    public ResponseEntity<UserSettingsDto> selectBackground(@UserId Long userId,
                                                            @PathVariable Long id,
                                                            @RequestBody SelectBackgroundRequest request) {
        permissionChecker.verifyPermission(userId, id, UserSettings.class);
        return response(userSettingsService.selectBackground(id, request));
    }

    @DeleteMapping(value = "/{id}/avatar")
    @Timed
    public ResponseEntity<Boolean> deleteUserAvatar(@UserId Long userId, @PathVariable Long id) {
        permissionChecker.verifyPermission(userId, id, UserSettings.class);
        userSettingsService.deleteUserAvatar(id);
        return response(true);
    }
}
