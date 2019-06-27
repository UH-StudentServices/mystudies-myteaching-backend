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

package fi.helsinki.opintoni.web.rest.privateapi.profile.contactinformation;

import fi.helsinki.opintoni.dto.profile.ContactInformationDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.security.authorization.TeacherRoleRequired;
import fi.helsinki.opintoni.service.profile.ContactInformationService;
import fi.helsinki.opintoni.service.profile.EmployeeContactInformationService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Locale;

import static fi.helsinki.opintoni.web.rest.RestConstants.MATCH_NUMBER;
import static fi.helsinki.opintoni.web.rest.RestConstants.PRIVATE_API_V1_PROFILE;

@RestController
@RequestMapping(
    value = PRIVATE_API_V1_PROFILE + "/{profileId:" + MATCH_NUMBER + "}/contactinformation",
    produces = WebConstants.APPLICATION_JSON_UTF8
)
public class PrivateContactInformationResource extends AbstractResource {

    private final ContactInformationService contactInformationService;
    private final EmployeeContactInformationService employeeContactInformationService;

    @Autowired
    public PrivateContactInformationResource(ContactInformationService contactInformationService,
                                             EmployeeContactInformationService employeeContactInformationService) {
        this.contactInformationService = contactInformationService;
        this.employeeContactInformationService = employeeContactInformationService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ContactInformationDto> update(
        @UserId Long userId,
        @PathVariable Long profileId,
        @Valid @RequestBody UpdateContactInformation request) {

        return response(contactInformationService.updateContactInformationWithSomeLinks(profileId, request));
    }

    @TeacherRoleRequired
    @RequestMapping(value = "/teacher", method = RequestMethod.GET)
    public ResponseEntity<ContactInformationDto> getEmployeeContactInformation(
        @PathVariable Long profileId,
        @AuthenticationPrincipal AppUser appUser,
        Locale locale) {

        ContactInformationDto contactInformationDto = appUser
            .getEmployeeNumber()
            .map(employeeNumber -> employeeContactInformationService.fetchEmployeeContactInformation(profileId, employeeNumber, locale))
            .orElseThrow(() -> new NotFoundException("No employee number found."));

        return response(contactInformationDto);
    }

}
