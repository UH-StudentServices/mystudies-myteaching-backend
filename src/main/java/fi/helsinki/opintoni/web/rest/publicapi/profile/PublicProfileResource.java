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

package fi.helsinki.opintoni.web.rest.publicapi.profile;

import fi.helsinki.opintoni.dto.profile.ProfileDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.service.converter.profile.ProfileConverter;
import fi.helsinki.opintoni.service.profile.ProfileService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.ProfileRole;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
    value = RestConstants.PUBLIC_API_V1_PROFILE,
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class PublicProfileResource extends AbstractResource {

    private final ProfileService profileService;

    @Autowired
    public PublicProfileResource(ProfileService profileService) {
        this.profileService = profileService;
    }

    @RequestMapping(value = "/{profileRole}/{lang}/{path:.*}", method = RequestMethod.GET)
    public ResponseEntity<ProfileDto> getByPath(
        @PathVariable("profileRole") String profileRole,
        @PathVariable("lang") String profileLang,
        @PathVariable("path") String path) {
        return response(profileService.findByPathAndLangAndRole(path,
            Language.fromCode(profileLang),
            ProfileRole.fromValue(profileRole),
            ProfileConverter.ComponentFetchStrategy.PUBLIC));
    }

    @GetMapping(value = "/shared/{sharedLinkFragment:.*}")
    public ResponseEntity<ProfileDto> getBySharedLink(@PathVariable("sharedLinkFragment") String sharedLinkFragment) {
        return response(profileService.findBySharedLink(sharedLinkFragment, ProfileConverter.ComponentFetchStrategy.PUBLIC));
    }
}
