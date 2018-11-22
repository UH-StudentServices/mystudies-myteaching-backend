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

import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.domain.profile.ProfileSharedLink;
import fi.helsinki.opintoni.dto.profile.ProfileSharedLinkDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.repository.profile.ProfileSharedLinkRepository;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.converter.profile.ProfileSharedLinkConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class ProfileSharedLinkService {

    private final ProfileSharedLinkRepository profileSharedLinkRepository;
    private final ProfileRepository profileRepository;
    private final ProfileSharedLinkConverter profileSharedLinkConverter;
    private final PermissionChecker permissionChecker;

    @Autowired
    public ProfileSharedLinkService(ProfileSharedLinkRepository profileSharedLinkRepository,
                                    ProfileRepository profileRepository,
                                    ProfileSharedLinkConverter profileSharedLinkConverter,
                                    PermissionChecker permissionChecker) {
        this.profileSharedLinkRepository = profileSharedLinkRepository;
        this.profileSharedLinkConverter = profileSharedLinkConverter;
        this.profileRepository = profileRepository;
        this.permissionChecker = permissionChecker;
    }

    public ProfileSharedLinkDto createSharedLink(final Long profileId, final Long userId, final ProfileSharedLinkDto sharedLinkDto) {
        permissionChecker.verifyPermission(userId, profileId, Profile.class);
        Profile profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);
        ProfileSharedLink sharedLink = new ProfileSharedLink();
        sharedLink.profile = profile;
        sharedLink.sharedPathFragment = UUID.randomUUID().toString();
        sharedLink.expiryDate = sharedLinkDto.expiryDate;

        return profileSharedLinkConverter.toDto(profileSharedLinkRepository.save(sharedLink));
    }

    public List<ProfileSharedLinkDto> getSharedLinks(final Long profileId, final Long userId) {
        permissionChecker.verifyPermission(userId, profileId, Profile.class);
        return profileSharedLinkRepository.findByProfileId(profileId).stream()
            .map(profileSharedLinkConverter::toDto)
            .collect(toList());
    }

    public void deleteSharedLink(final Long profileId, final Long userId, final Long sharedLinkId) {
        permissionChecker.verifyPermission(userId, profileId, Profile.class);
        profileSharedLinkRepository.deleteById(sharedLinkId);
    }
}
