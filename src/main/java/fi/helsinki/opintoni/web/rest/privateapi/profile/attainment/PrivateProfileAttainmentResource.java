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

package fi.helsinki.opintoni.web.rest.privateapi.profile.attainment;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.dto.StudyAttainmentDto;
import fi.helsinki.opintoni.dto.profile.StudyAttainmentWhitelistDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.profile.ProfileStudyAttainmentWhitelistService;
import fi.helsinki.opintoni.service.profile.StudyAttainmentService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1_PROFILE + "/{profileId:" + RestConstants.MATCH_NUMBER + "}/attainment",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateProfileAttainmentResource extends AbstractResource {

    private final ProfileStudyAttainmentWhitelistService whitelistService;

    private final PermissionChecker permissionChecker;

    private final StudyAttainmentService studyAttainmentService;

    @Autowired
    public PrivateProfileAttainmentResource(ProfileStudyAttainmentWhitelistService whitelistService,
                                            PermissionChecker permissionChecker,
                                            StudyAttainmentService studyAttainmentService) {
        this.whitelistService = whitelistService;
        this.permissionChecker = permissionChecker;
        this.studyAttainmentService = studyAttainmentService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/whitelist")
    @Timed
    public ResponseEntity<StudyAttainmentWhitelistDto> update(@UserId Long userId,
                                                              @PathVariable Long profileId,
                                                              @Valid @RequestBody
                                                              StudyAttainmentWhitelistDto whitelistDto) {
        permissionChecker.verifyPermission(userId, profileId, Profile.class);
        return response(whitelistService.update(profileId, whitelistDto));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/whitelist")
    @Timed
    public ResponseEntity<StudyAttainmentWhitelistDto> getStudyAttainmentWhitelist(@UserId Long userId,
                                                                                   @PathVariable Long profileId) {
        permissionChecker.verifyPermission(userId, profileId, Profile.class);
        return response(whitelistService.get(profileId));
    }

    @RequestMapping(method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<StudyAttainmentDto>> getWhitelistedAttainmentsByProfileId(
        @PathVariable Long profileId,
        @RequestParam(value = "lang", defaultValue = "fi") String langCode) {
        Locale locale = Locale.forLanguageTag(langCode);
        return response(studyAttainmentService.getWhitelistedAttainmentsByProfileId(profileId, locale));
    }

}
