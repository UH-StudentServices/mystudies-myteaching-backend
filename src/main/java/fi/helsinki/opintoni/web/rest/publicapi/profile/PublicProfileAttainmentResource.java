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

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.dto.StudyAttainmentDto;
import fi.helsinki.opintoni.security.authorization.profile.PublicVisibility;
import fi.helsinki.opintoni.service.profile.StudyAttainmentService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(
    value = RestConstants.PUBLIC_API_V1_PROFILE,
    produces = WebConstants.APPLICATION_JSON_UTF8)
@PublicVisibility(ProfileComponent.ATTAINMENTS)
public class PublicProfileAttainmentResource extends AbstractResource {

    private final StudyAttainmentService studyAttainmentService;

    @Autowired
    public PublicProfileAttainmentResource(StudyAttainmentService studyAttainmentService) {
        this.studyAttainmentService = studyAttainmentService;
    }

    @GetMapping("/{profileId:" + RestConstants.MATCH_NUMBER + "}/attainment")
    @Timed
    public ResponseEntity<List<StudyAttainmentDto>> getStudyAttainments(@PathVariable Long profileId, Locale locale) {
        return response(studyAttainmentService.getWhitelistedAttainmentsByProfileId(profileId, locale));
    }

    @GetMapping("/{sharedLinkFragment:.*}/attainment")
    @Timed
    public ResponseEntity<List<StudyAttainmentDto>> getStudyAttainmentsWithSharedLink(@PathVariable String sharedLinkFragment, Locale locale) {
        return response(studyAttainmentService.getWhiteListedAttainmentsBySharedLink(sharedLinkFragment, locale));
    }
}
