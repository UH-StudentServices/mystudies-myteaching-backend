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

package fi.helsinki.opintoni.web.rest.publicapi;

import fi.helsinki.opintoni.dto.UserProfileDto;
import fi.helsinki.opintoni.service.UserService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static fi.helsinki.opintoni.exception.http.NotFoundException.notFoundException;

@RestController
@RequestMapping(
    value = RestConstants.PUBLIC_API_V1 + "/profile",
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class PublicUserProfileResource extends AbstractResource {

    private final UserService userService;

    @Autowired
    public PublicUserProfileResource(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/{oodiPersonId}", method = RequestMethod.GET)
    public ResponseEntity<UserProfileDto> get(@PathVariable("oodiPersonId") String oodiPersonId) {
        return response(userService.getProfileByOodiPersonId(oodiPersonId)
            .orElseThrow(notFoundException("User profile not found with oodiPersonId " + oodiPersonId)));
    }
}
