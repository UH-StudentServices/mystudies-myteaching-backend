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

package fi.helsinki.opintoni.web.rest.privateapi.profile.summary;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.profile.ProfileService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1_PROFILE + "/{profileId:" + RestConstants.MATCH_NUMBER + "}/summary",
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class PrivateSummaryResource extends AbstractResource {

    private final PermissionChecker permissionChecker;
    private final ProfileService profileService;

    @Autowired
    public PrivateSummaryResource(PermissionChecker permissionChecker, ProfileService profileService) {
        this.permissionChecker = permissionChecker;
        this.profileService = profileService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    public ResponseEntity<Boolean> update(@UserId Long userId,
                                          @PathVariable Long profileId,
                                          @Valid @RequestBody UpdateSummaryRequest request) {
        permissionChecker.verifyPermission(userId, profileId, Profile.class);
        profileService.updateSummary(profileId, request);
        return response(true);
    }
}
