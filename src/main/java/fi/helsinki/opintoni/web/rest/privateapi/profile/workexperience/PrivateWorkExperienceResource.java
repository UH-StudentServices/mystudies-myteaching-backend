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

package fi.helsinki.opintoni.web.rest.privateapi.profile.workexperience;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.dto.profile.WorkExperienceDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.profile.WorkExperienceService;
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

import java.util.List;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1_PROFILE + "/{profileId:" + RestConstants.MATCH_NUMBER + "}/workexperience",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateWorkExperienceResource extends AbstractResource {

    private final WorkExperienceService workExperienceService;
    private final PermissionChecker permissionChecker;

    @Autowired
    public PrivateWorkExperienceResource(WorkExperienceService workExperienceService,
                                         PermissionChecker permissionChecker) {
        this.workExperienceService = workExperienceService;
        this.permissionChecker = permissionChecker;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    public ResponseEntity<List<WorkExperienceDto>> update(@UserId Long userId,
                                                  @PathVariable Long profileId,
                                                  @RequestBody List<UpdateWorkExperience> updateWorkExperiences) {
        permissionChecker.verifyPermission(userId, profileId, Profile.class);
        return response(workExperienceService.updateWorkExperiences(profileId, updateWorkExperiences));
    }
}
