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

package fi.helsinki.opintoni.service.profile;

import fi.helsinki.opintoni.dto.profile.ContactInformationDto;
import fi.helsinki.opintoni.dto.profile.ProfileDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.web.arguments.ProfileRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EmployeeProfileService {
    private final ProfileService profileService;
    private final EmployeeContactInformationService employeeContactInformationService;

    @Autowired
    public EmployeeProfileService(ProfileService profileService,
                                  EmployeeContactInformationService employeeContactInformationService) {
        this.profileService = profileService;
        this.employeeContactInformationService = employeeContactInformationService;
    }

    public ProfileDto insert(Long userId, AppUser appUser, Locale locale) {

        ProfileDto profileDto = profileService.insert(userId, appUser.getCommonName(), ProfileRole.TEACHER,
            Language.fromCode(locale.getLanguage()));

        ContactInformationDto contactInformationDto = appUser.getEmployeeNumber()
            .map(employeeNumber -> employeeContactInformationService.fetchAndSaveEmployeeContactInformation(profileDto.id, employeeNumber, locale))
            .orElse(null);

        profileDto.contactInformation = contactInformationDto;

        return profileDto;
    }
}
