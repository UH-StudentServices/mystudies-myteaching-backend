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

import fi.helsinki.opintoni.domain.profile.ComponentVisibility;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.domain.profile.ProfileSharedLink;
import fi.helsinki.opintoni.domain.profile.ProfileVisibility;
import fi.helsinki.opintoni.domain.profile.TeacherProfileSection;
import fi.helsinki.opintoni.dto.profile.ProfileDto;
import fi.helsinki.opintoni.exception.http.ForbiddenException;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.repository.profile.ProfileSharedLinkRepository;
import fi.helsinki.opintoni.service.ImageService;
import fi.helsinki.opintoni.service.converter.profile.ProfileConverter;
import fi.helsinki.opintoni.web.arguments.ProfileRole;
import fi.helsinki.opintoni.web.rest.privateapi.profile.summary.UpdateSummaryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import static fi.helsinki.opintoni.exception.http.NotFoundException.notFoundException;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileConverter profileConverter;
    private final ProfileStudyAttainmentWhitelistService whitelistService;
    private final ComponentVisibilityService componentVisibilityService;
    private final ProfileSharedLinkRepository profileSharedLinkRepository;
    private final ImageService imageService;

    @Autowired
    public ProfileService(ProfileRepository profileRepository,
                          UserRepository userRepository,
                          ProfileConverter profileConverter,
                          ProfileStudyAttainmentWhitelistService whitelistService,
                          ComponentVisibilityService componentVisibilityService,
                          ProfileSharedLinkRepository profileSharedLinkRepository,
                          ImageService imageService) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.profileConverter = profileConverter;
        this.whitelistService = whitelistService;
        this.componentVisibilityService = componentVisibilityService;
        this.profileSharedLinkRepository = profileSharedLinkRepository;
        this.imageService = imageService;
    }

    public ProfileDto insert(Long userId, String name, ProfileRole profileRole, Language lang) {
        String profilePath = profileRepository
            .findByUserId(userId)
            .findFirst()
            .map(profile -> profile.path)
            .orElseThrow(() -> new ForbiddenException("User [" + userId + "] does not have existing profile"));

        Profile profile = new Profile();
        profile.language = lang;
        profile.user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        profile.path = profilePath;
        profile.ownerName = name;
        profile.visibility = ProfileVisibility.PRIVATE;
        profile.profileRole = profileRole;
        Profile inserted = profileRepository.save(profile);

        if (profileRole == ProfileRole.TEACHER) {
            insertTeacherProfileSectionVisibilities(profile);
        }

        whitelistService.insert(inserted);

        return profileConverter.toDto(inserted, ProfileConverter.ComponentFetchStrategy.NONE);
    }

    public ProfileDto get(Long userId, ProfileRole profileRole) {
        return convertProfileToDto(
            profileRepository.findByUserIdAndProfileRole(userId, profileRole).findFirst(),
            ProfileConverter.ComponentFetchStrategy.ALL,
            ProfileUrlContext.EMPTY);
    }

    public ProfileDto findByPathAndLangAndRole(String path,
                                               Language lang,
                                               ProfileRole profileRole,
                                               ProfileConverter.ComponentFetchStrategy componentFetchStrategy) {
        return findByPathAndLangAndRole(path, lang, profileRole, componentFetchStrategy, ProfileUrlContext.EMPTY);
    }

    public ProfileDto findByPathAndLangAndRole(String path,
                                               Language lang,
                                               ProfileRole profileRole,
                                               ProfileConverter.ComponentFetchStrategy componentFetchStrategy,
                                               ProfileUrlContext profileUrlContext) {
        return convertProfileToDto(profileRepository
            .findByPathAndProfileRoleAndLanguage(path, profileRole, lang), componentFetchStrategy, profileUrlContext);
    }

    public ProfileDto findBySharedLink(String sharedLinkFragment, ProfileConverter.ComponentFetchStrategy componentFetchStrategy,
                                       ProfileUrlContext profileUrlContext) {
        ProfileSharedLink sharedLink = profileSharedLinkRepository
            .findBySharedPathFragment(sharedLinkFragment)
            .orElseThrow(NotFoundException::new);

        if (!sharedLink.isActive()) {
            throw new NotFoundException("Profile not found");
        }

        Profile profile = profileRepository.findById(sharedLink.profile.id).orElseThrow(NotFoundException::new);
        ProfileDto profileDto = profileConverter.toDto(profile, componentFetchStrategy, profileUrlContext);

        // Fake public visibility when using shared link
        profileDto.visibility = ProfileVisibility.PUBLIC;

        return profileDto;
    }

    public ProfileDto findById(Long profileId) {
        return convertProfileToDto(profileRepository
            .findById(profileId), ProfileConverter.ComponentFetchStrategy.NONE, ProfileUrlContext.EMPTY);
    }

    private ProfileDto convertProfileToDto(Optional<Profile> profileOptional,
                                           ProfileConverter.ComponentFetchStrategy componentFetchStrategy,
                                           ProfileUrlContext profileUrlContext) {
        return profileOptional
            .map((profile) -> profileConverter.toDto(profile, componentFetchStrategy, profileUrlContext))
            .orElseThrow(notFoundException("profile not found"));
    }

    public Map<String, Map<String, List<String>>> getUserProfilePathsByRoleAndLang(Long userId) {
        return profileRepository
            .findByUserId(userId)
            .collect(groupingBy(
                profile -> profile.profileRole.getRole(),
                groupingBy(
                    profile -> profile.language.getCode(),
                    mapping(
                        ProfileService::profilePath,
                        toList()
                    )
                )
            ));
    }

    public BufferedImage getProfileImageByPath(String path) {
        Optional<byte[]> imageBytes = Optional.ofNullable(profileRepository.getProfileImageByByPath(path));
        return imageBytes.map(imageService::bytesToBufferedImage)
            .orElseThrow(notFoundException("Avatar not found for path " + path));
    }

    public BufferedImage getProfileImageBySharedLinkFragment(String sharedLinkFragment) {
        Optional<byte[]> imageBytes = Optional.ofNullable(profileRepository.getProfileImageBySharedLinkFragment(sharedLinkFragment));
        return imageBytes.map(imageService::bytesToBufferedImage)
            .orElseThrow(notFoundException("Avatar not found for shared link fragment " + sharedLinkFragment));
    }

    public ProfileDto update(Long profileId, ProfileDto profileDto) {
        Profile profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);
        profile.visibility = profileDto.visibility;
        profile.ownerName = profileDto.ownerName;
        profile.intro = profileDto.intro;

        return profileConverter.toDto(profileRepository.save(profile), ProfileConverter.ComponentFetchStrategy.NONE);
    }

    public void updateSummary(Long profileId, UpdateSummaryRequest request) {
        Profile profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);
        profile.summary = request.summary;
        profileRepository.save(profile);
    }

    private void insertTeacherProfileSectionVisibilities(Profile profile) {
        List<ComponentVisibility> sectionVisibilities = Arrays.stream(TeacherProfileSection.values())
            .map(section -> {
                ComponentVisibility visibility = new ComponentVisibility();
                visibility.teacherProfileSection = section;
                visibility.visibility = section == TeacherProfileSection.BASIC_INFORMATION ?
                    ComponentVisibility.Visibility.PUBLIC :
                    ComponentVisibility.Visibility.PRIVATE;
                visibility.profile = profile;

                return visibility;
            }).collect(toList());

        componentVisibilityService.save(sectionVisibilities);
    }

    private static String profilePath(Profile profile) {
        return new StringJoiner("/", "/", "")
            .add(profile.language.getCode())
            .add(profile.path)
            .toString();
    }

    public static class ProfileUrlContext {
        public final String fullPath;
        public final String sharedLinkFragment;

        public static final ProfileUrlContext EMPTY = new ProfileUrlContext("no-profile-uri-context", null);

        public ProfileUrlContext(String fullPath, String sharedLinkFragment) {
            this.fullPath = fullPath;
            this.sharedLinkFragment = sharedLinkFragment;
        }
    }
}
