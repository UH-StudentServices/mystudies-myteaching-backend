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

package fi.helsinki.opintoni.web.rest.privateapi.profile.keyword;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.dto.profile.KeywordDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.profile.ProfileKeywordRelationshipService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1_PROFILE + "/{profileId:" + RestConstants.MATCH_NUMBER + "}/keyword",
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class PrivateProfileKeywordRelationshipResource extends AbstractResource {

    private final ProfileKeywordRelationshipService profileKeywordRelationshipService;
    private final PermissionChecker permissionChecker;

    @Autowired
    public PrivateProfileKeywordRelationshipResource(
        ProfileKeywordRelationshipService profileKeywordRelationshipService,
        PermissionChecker permissionChecker) {
        this.profileKeywordRelationshipService = profileKeywordRelationshipService;
        this.permissionChecker = permissionChecker;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    public ResponseEntity<List<KeywordDto>> update(@UserId Long userId,
                                                   @PathVariable Long profileId,
                                                   @Valid @RequestBody
                                                   UpdateKeywordsRequest updateKeywordsRequest) {
        permissionChecker.verifyPermission(userId, profileId, Profile.class);
        return response(profileKeywordRelationshipService.update(profileId, updateKeywordsRequest));
    }
}
