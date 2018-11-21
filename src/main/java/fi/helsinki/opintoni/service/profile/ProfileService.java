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

import fi.helsinki.opintoni.domain.profile.*;
import fi.helsinki.opintoni.dto.profile.ProfileDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.repository.profile.ProfileSharedLinkRepository;
import fi.helsinki.opintoni.service.converter.profile.ProfileConverter;
import fi.helsinki.opintoni.web.arguments.ProfileRole;
import fi.helsinki.opintoni.web.rest.privateapi.profile.summary.UpdateSummaryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static fi.helsinki.opintoni.exception.http.NotFoundException.notFoundException;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfilePathGenerator profilePathGenerator;
    private final ProfileConverter profileConverter;
    private final ProfileStudyAttainmentWhitelistService whitelistService;
    private final ComponentVisibilityService componentVisibilityService;
    private final ProfileSharedLinkRepository profileSharedLinkRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository,
                          UserRepository userRepository,
                          ProfilePathGenerator profilePathGenerator,
                          ProfileConverter profileConverter,
                          ProfileStudyAttainmentWhitelistService whitelistService,
                          ComponentVisibilityService componentVisibilityService,
                          ProfileSharedLinkRepository profileSharedLinkRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.profilePathGenerator = profilePathGenerator;
        this.profileConverter = profileConverter;
        this.whitelistService = whitelistService;
        this.componentVisibilityService = componentVisibilityService;
        this.profileSharedLinkRepository = profileSharedLinkRepository;
    }

    public ProfileDto insert(Long userId, String name, ProfileRole profileRole, Language lang) {
        String profilePath = profileRepository
            .findByUserId(userId)
            .findFirst()
            .map(profile -> profile.path)
            .orElse(profilePathGenerator.create(name));

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
            ProfileConverter.ComponentFetchStrategy.ALL);
    }

    public ProfileDto findByPathAndLangAndRole(String path,
                                               Language lang,
                                               ProfileRole profileRole,
                                               ProfileConverter.ComponentFetchStrategy componentFetchStrategy) {
        return convertProfileToDto(profileRepository
            .findByPathAndProfileRoleAndLanguage(path, profileRole, lang), componentFetchStrategy);
    }

    public ProfileDto findBySharedLink(String sharedLinkFragment, ProfileConverter.ComponentFetchStrategy componentFetchStrategy) {
        ProfileSharedLink sharedLink = profileSharedLinkRepository
            .findBySharedPathFragment(sharedLinkFragment)
            .orElseThrow(NotFoundException::new);

        if (!sharedLink.isActive()) {
            throw new NotFoundException("Profile not found");
        }

        Profile profile = profileRepository.findById(sharedLink.profile.id).orElseThrow(NotFoundException::new);
        ProfileDto profileDto = profileConverter.toDto(profile, componentFetchStrategy, sharedLinkFragment);

        // Fake public visibility when using shared link
        profileDto.visibility = ProfileVisibility.PUBLIC;

        return profileDto;
    }

    public ProfileDto findById(Long profileId) {
        return convertProfileToDto(profileRepository
            .findById(profileId), ProfileConverter.ComponentFetchStrategy.NONE);
    }

    private ProfileDto convertProfileToDto(Optional<Profile> profileOptional,
                                           ProfileConverter.ComponentFetchStrategy componentFetchStrategy) {
        return profileOptional
            .map((profile) -> profileConverter.toDto(profile, componentFetchStrategy))
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
}
