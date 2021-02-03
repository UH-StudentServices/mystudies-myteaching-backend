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

package fi.helsinki.opintoni.web.rest.privateapi.profile;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.dto.profile.ProfileDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.security.authorization.StudentRoleRequired;
import fi.helsinki.opintoni.security.authorization.TeacherRoleRequired;
import fi.helsinki.opintoni.service.UserSettingsService;
import fi.helsinki.opintoni.service.converter.profile.ProfileConverter;
import fi.helsinki.opintoni.service.profile.EmployeeProfileService;
import fi.helsinki.opintoni.service.profile.ProfileService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.arguments.ProfileRole;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.util.Locale;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1_PROFILE,
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class PrivateProfileResource extends AbstractResource {
    private final ProfileService profileService;
    private final EmployeeProfileService employeeProfileService;

    @Autowired
    public PrivateProfileResource(ProfileService profileService,
                                  EmployeeProfileService employeeProfileService,
                                  UserSettingsService userSettingsService) {
        this.profileService = profileService;
        this.employeeProfileService = employeeProfileService;
    }

    @RequestMapping(value = "/{profileRole}", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<ProfileDto> get(@PathVariable("profileRole") String profileRole,
                                          @UserId Long userId) {
        return response(profileService.get(userId, ProfileRole.fromValue(profileRole)));
    }

    @RequestMapping(value = "/student", method = RequestMethod.POST)
    @Timed
    @StudentRoleRequired
    public ResponseEntity<ProfileDto> createStudentProfileInSessionLang(@UserId Long userId,
                                                                        @AuthenticationPrincipal AppUser appUser,
                                                                        Locale locale) {
        return response(profileService.insert(
            userId,
            appUser.getCommonName(),
            ProfileRole.STUDENT,
            Language.fromCode(locale.getLanguage())));
    }

    @RequestMapping(value = "/teacher", method = RequestMethod.POST)
    @Timed
    @TeacherRoleRequired
    public ResponseEntity<ProfileDto> createTeacherProfileInSessionLang(@UserId Long userId,
                                                                        @AuthenticationPrincipal AppUser appUser,
                                                                        Locale locale) {
        return response(employeeProfileService.insert(
            userId,
            appUser,
            locale));
    }

    @RequestMapping(value = "/student/{lang}", method = RequestMethod.POST)
    @Timed
    @StudentRoleRequired
    public ResponseEntity<ProfileDto> insertStudentProfile(@UserId Long userId,
                                                           @AuthenticationPrincipal AppUser appUser,
                                                           @PathVariable("lang") String langCode) {
        return response(profileService.insert(
            userId,
            appUser.getCommonName(),
            ProfileRole.STUDENT,
            Language.fromCode(langCode)));
    }

    @RequestMapping(value = "/teacher/{lang}", method = RequestMethod.POST)
    @Timed
    @TeacherRoleRequired
    public ResponseEntity<ProfileDto> insertTeacherProfile(@UserId Long userId,
                                                           @AuthenticationPrincipal AppUser appUser,
                                                           @PathVariable("lang") String langCode) {
        return response(employeeProfileService.insert(
            userId,
            appUser,
            Language.fromCode(langCode).toLocale()));
    }

    @RequestMapping(value = "/{profileRole}/{lang}/{path:.*}", method = RequestMethod.GET)
    public ResponseEntity<ProfileDto> findByPath(
        @PathVariable("profileRole") String profileRole,
        @PathVariable("lang") String profileLang,
        @PathVariable("path") String path) {
        ProfileDto profileDto = profileService.findByPathAndLangAndRole(path,
            Language.fromCode(profileLang),
            ProfileRole.fromValue(profileRole),
            ProfileConverter.ComponentFetchStrategy.ALL,
            new ProfileService.ProfileUrlContext(String.join("/", RestConstants.PRIVATE_API_V1_PROFILE, profileRole, profileLang, path), null));
        return response(profileDto);
    }

    @RequestMapping(value = "/{profileId}", method = RequestMethod.PUT)
    public ResponseEntity<ProfileDto> update(
        @PathVariable("profileId") Long profileId,
        @Valid @RequestBody ProfileDto profileDto) {
        return response(profileService.update(profileId, profileDto));
    }

    @RequestMapping(value = "/{profileRole}/{lang}/{path:.*}/profile-image",
        method = RequestMethod.GET,
        produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<BufferedImage> getMyProfileImage(@PathVariable("path") String path) {
        return ResponseEntity.ok()
            .headers(headersWithContentType(MediaType.IMAGE_JPEG))
            .body(profileService.getProfileImageByPath(path));
    }
}
