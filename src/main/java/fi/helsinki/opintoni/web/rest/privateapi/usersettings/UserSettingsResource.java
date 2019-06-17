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
import fi.helsinki.opintoni.dto.UserSettingsDto;
import fi.helsinki.opintoni.service.UserSettingsService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Autowired
    public UserSettingsResource(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @GetMapping
    @Timed
    public ResponseEntity<UserSettingsDto> get(@UserId Long userId) {
        return response(userSettingsService.findByUserId(userId));
    }

    @PutMapping
    @Timed
    public ResponseEntity<UserSettingsDto> update(@UserId Long userId, @RequestBody UpdateUserSettingsRequest request) {
        return response(userSettingsService.update(userId, request));
    }

    @PostMapping(value = "/avatar")
    @Timed
    public ResponseEntity<Boolean> uploadUserAvatar(@UserId Long userId, @RequestBody UploadImageBase64Request request) {
        userSettingsService.updateUserAvatar(userId, request.imageBase64);
        return response(true);
    }

    @PostMapping(value = "/background")
    @Timed
    public ResponseEntity<UserSettingsDto> uploadBackground(@UserId Long userId,
                                                            @RequestBody UploadImageBase64Request request) {
        return response(userSettingsService.updateBackground(userId, request));
    }

    @PostMapping(value = "/background/select")
    @Timed
    public ResponseEntity<UserSettingsDto> selectBackground(@UserId Long userId,
                                                            @RequestBody SelectBackgroundRequest request) {
        return response(userSettingsService.selectBackground(userId, request));
    }

    @DeleteMapping(value = "/avatar")
    @Timed
    public ResponseEntity<Boolean> deleteUserAvatar(@UserId Long userId) {
        userSettingsService.deleteUserAvatar(userId);
        return response(true);
    }
}
