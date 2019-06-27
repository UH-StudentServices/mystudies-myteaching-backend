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

package fi.helsinki.opintoni.web.rest.privateapi.profile.profilesharedlink;

import fi.helsinki.opintoni.dto.profile.ProfileSharedLinkDto;
import fi.helsinki.opintoni.service.profile.ProfileSharedLinkService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fi.helsinki.opintoni.web.rest.RestConstants.MATCH_NUMBER;
import static fi.helsinki.opintoni.web.rest.RestConstants.PRIVATE_API_V1_PROFILE;

@RestController
@RequestMapping(
    value = PRIVATE_API_V1_PROFILE + "/{profileId:" + MATCH_NUMBER + "}/sharedlinks",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateProfileSharedLinkResource extends AbstractResource {

    private final ProfileSharedLinkService profileSharedLinkService;

    @Autowired
    public PrivateProfileSharedLinkResource(ProfileSharedLinkService profileSharedLinkService) {
        this.profileSharedLinkService = profileSharedLinkService;
    }

    @GetMapping
    public ResponseEntity<List<ProfileSharedLinkDto>> getSharedLinks(@PathVariable Long profileId,
                                                                     @UserId Long userId) {
        return response(profileSharedLinkService.getSharedLinks(profileId, userId));
    }

    @PostMapping
    public ResponseEntity<ProfileSharedLinkDto> createSharedLink(@PathVariable Long profileId,
                                                                 @RequestBody ProfileSharedLinkDto sharedLinkDto,
                                                                 @UserId Long userId) {
        return response(profileSharedLinkService.createSharedLink(profileId, userId, sharedLinkDto));
    }

    @DeleteMapping(value = "/{sharedLinkId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteSharedLink(@PathVariable Long profileId,
                                 @PathVariable Long sharedLinkId,
                                 @UserId Long userId) {
        profileSharedLinkService.deleteSharedLink(profileId, userId, sharedLinkId);
    }
}
