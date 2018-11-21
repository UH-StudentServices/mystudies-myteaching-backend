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

package fi.helsinki.opintoni.web.rest.privateapi.profile.freetextcontent;

import fi.helsinki.opintoni.domain.profile.FreeTextContent;
import fi.helsinki.opintoni.dto.profile.FreeTextContentDto;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.profile.FreeTextContentService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static fi.helsinki.opintoni.web.rest.RestConstants.MATCH_NUMBER;
import static fi.helsinki.opintoni.web.rest.RestConstants.PRIVATE_API_V1_PROFILE;

@RestController
@RequestMapping(
    value = PRIVATE_API_V1_PROFILE + "/{profileId:" + MATCH_NUMBER + "}/freetextcontent",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateFreeTextContentResource extends AbstractResource {

    private final PermissionChecker permissionChecker;
    private final FreeTextContentService freeTextContentService;

    @Autowired
    public PrivateFreeTextContentResource(PermissionChecker permissionChecker, FreeTextContentService freeTextContentService) {
        this.permissionChecker = permissionChecker;
        this.freeTextContentService = freeTextContentService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<FreeTextContentDto> insertFreeTextContent(@PathVariable Long profileId,
                                                                    @RequestBody FreeTextContentDto freeTextContentDto) {
        return response(freeTextContentService.insertFreeTextContent(profileId, freeTextContentDto));
    }

    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/{freeTextContentId}")
    public ResponseEntity<FreeTextContentDto> updateFreeTextContent(@UserId Long userId,
                                                @PathVariable Long freeTextContentId,
                                                @RequestBody FreeTextContentDto freeTextContentDto) {
        permissionChecker.verifyPermission(userId, freeTextContentId, FreeTextContent.class);
        return response(freeTextContentService.updateFreeTextContent(freeTextContentId, freeTextContentDto));
    }

    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/{freeTextContentId}")
    public ResponseEntity deleteFreeTextContent(@UserId Long userId,
                                                @PathVariable Long profileId,
                                                @PathVariable Long freeTextContentId,
                                                @RequestParam(value = "instanceName") String instanceName) {
        permissionChecker.verifyPermission(userId, freeTextContentId, FreeTextContent.class);
        freeTextContentService.deleteFreeTextContent(freeTextContentId, profileId, instanceName);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
