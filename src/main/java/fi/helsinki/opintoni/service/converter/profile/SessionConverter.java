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

package fi.helsinki.opintoni.service.converter.profile;

import fi.helsinki.opintoni.dto.SessionDto;
import fi.helsinki.opintoni.integration.studyregistry.oodi.OodiIntegrationException;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.service.AvatarImageService;
import fi.helsinki.opintoni.service.OodiUserService;
import fi.helsinki.opintoni.service.converter.FacultyConverter;
import fi.helsinki.opintoni.service.profile.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SessionConverter {

    private final ProfileService profileService;
    private final OodiUserService oodiUserService;
    private final FacultyConverter facultyConverter;
    private final AvatarImageService avatarImageService;

    @Autowired
    public SessionConverter(ProfileService profileService,
                            OodiUserService oodiUserService,
                            FacultyConverter facultyConverter,
                            AvatarImageService avatarImageService) {
        this.profileService = profileService;
        this.oodiUserService = oodiUserService;
        this.facultyConverter = facultyConverter;
        this.avatarImageService = avatarImageService;
    }

    public SessionDto toDto(AppUser appUser, Long userId) {
        SessionDto sessionDto = new SessionDto();
        sessionDto.username = appUser.getUsername();
        sessionDto.name = appUser.getCommonName();
        sessionDto.email = appUser.getEmail();
        sessionDto.avatarUrl = avatarImageService.getAvatarImageUrl(userId);
        sessionDto.profilePathsByRoleAndLang = profileService.getUserProfilePathsByRoleAndLang(userId);
        sessionDto.roles = convertAuthoritiesToText(appUser);

        try {
            sessionDto.openUniversity = oodiUserService.isOpenUniversityUser(appUser);
            sessionDto.faculty = facultyConverter.getFacultyDto(appUser);
        } catch (OodiIntegrationException e) {
            sessionDto.openUniversity = false;
        }

        return sessionDto;
    }

    private Set<String> convertAuthoritiesToText(AppUser appUser) {
        return appUser.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
    }
}
