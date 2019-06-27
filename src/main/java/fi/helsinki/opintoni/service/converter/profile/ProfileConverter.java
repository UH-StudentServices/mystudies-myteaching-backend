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

import fi.helsinki.opintoni.domain.profile.ComponentVisibility;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.domain.profile.TeacherProfileSection;
import fi.helsinki.opintoni.dto.profile.ComponentVisibilityDto;
import fi.helsinki.opintoni.dto.profile.ProfileDto;
import fi.helsinki.opintoni.dto.profile.SummaryDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.AvatarImageService;
import fi.helsinki.opintoni.service.profile.ComponentHeadingService;
import fi.helsinki.opintoni.service.profile.ComponentOrderService;
import fi.helsinki.opintoni.service.profile.ComponentVisibilityService;
import fi.helsinki.opintoni.service.profile.ContactInformationService;
import fi.helsinki.opintoni.service.profile.DegreeService;
import fi.helsinki.opintoni.service.profile.FreeTextContentService;
import fi.helsinki.opintoni.service.profile.JobSearchService;
import fi.helsinki.opintoni.service.profile.LanguageProficiencyService;
import fi.helsinki.opintoni.service.profile.ProfileBackgroundService;
import fi.helsinki.opintoni.service.profile.ProfileKeywordRelationshipService;
import fi.helsinki.opintoni.service.profile.ProfileService;
import fi.helsinki.opintoni.service.profile.SampleService;
import fi.helsinki.opintoni.service.profile.WorkExperienceService;
import fi.helsinki.opintoni.util.UriBuilder;
import fi.helsinki.opintoni.web.arguments.ProfileRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fi.helsinki.opintoni.domain.profile.ComponentVisibility.Visibility.PUBLIC;

@Component
public class ProfileConverter {

    private final UriBuilder uriBuilder;
    private final ComponentVisibilityService componentVisibilityService;
    private final ComponentOrderService componentOrderService;
    private final ComponentHeadingService componentHeadingService;
    private final AvatarImageService avatarImageService;
    private final LanguageProficiencyService languageProficiencyService;
    private final FreeTextContentService freeTextContentService;
    private final WorkExperienceService workExperienceService;
    private final SampleService sampleService;
    private final JobSearchService jobSearchService;
    private final ContactInformationService contactInformationService;
    private final DegreeService degreeService;
    private final ProfileKeywordRelationshipService keywordRelationshipService;
    private final ProfileRepository profileRepository;
    private final ProfileBackgroundService profileBackgroundService;

    @Autowired
    public ProfileConverter(UriBuilder uriBuilder,
                            ComponentVisibilityService componentVisibilityService,
                            ComponentOrderService componentOrderService,
                            ComponentHeadingService componentHeadingService,
                            AvatarImageService avatarImageService,
                            LanguageProficiencyService languageProficiencyService,
                            FreeTextContentService freeTextContentService,
                            WorkExperienceService workExperienceService,
                            SampleService sampleService,
                            JobSearchService jobSearchService,
                            ContactInformationService contactInformationService,
                            DegreeService degreeService,
                            ProfileKeywordRelationshipService keywordRelationshipService,
                            ProfileRepository profileRepository,
                            ProfileBackgroundService profileBackgroundService) {
        this.uriBuilder = uriBuilder;
        this.componentVisibilityService = componentVisibilityService;
        this.componentOrderService = componentOrderService;
        this.componentHeadingService = componentHeadingService;
        this.avatarImageService = avatarImageService;
        this.languageProficiencyService = languageProficiencyService;
        this.freeTextContentService = freeTextContentService;
        this.workExperienceService = workExperienceService;
        this.sampleService = sampleService;
        this.jobSearchService = jobSearchService;
        this.contactInformationService = contactInformationService;
        this.degreeService = degreeService;
        this.keywordRelationshipService = keywordRelationshipService;
        this.profileRepository = profileRepository;
        this.profileBackgroundService = profileBackgroundService;
    }

    public ProfileDto toDto(Profile profile, ComponentFetchStrategy componentFetchStrategy) {
        return toDto(profile, componentFetchStrategy, ProfileService.ProfileUrlContext.EMPTY);
    }

    public ProfileDto toDto(Profile profile, ComponentFetchStrategy componentFetchStrategy, ProfileService.ProfileUrlContext profileUrlContext) {
        ProfileDto profileDto = new ProfileDto();
        profileDto.id = profile.id;
        profileDto.lang = profile.language.getCode();
        profileDto.url =  profileUrlContext.sharedLinkFragment == null ?
            uriBuilder.getProfileUrl(profile) :
            uriBuilder.getProfileUrl(profile, profileUrlContext.sharedLinkFragment);
        profileDto.intro = profile.intro;
        profileDto.ownerName = profile.ownerName;
        profileDto.backgroundUri = profileBackgroundService.getProfileBackgroundUri(profile);
        profileDto.visibility = profile.visibility;
        profileDto.avatarUrl = avatarImageService.getProfileAvatarImageUrl(profile.user.id, profileUrlContext);
        profileDto.componentVisibilities = componentVisibilityService.findByProfileId(profile.id);
        profileDto.componentOrders = componentOrderService.findByProfileId(profile.id);
        profileDto.headings = componentHeadingService.findByProfileId(profile.id);

        fetchProfileComponents(profile, profileDto, componentFetchStrategy);

        return profileDto;
    }

    private void fetchProfileComponents(Profile profile,
                                        ProfileDto profileDto,
                                        ComponentFetchStrategy componentFetchStrategy) {
        if (componentFetchStrategy == ComponentFetchStrategy.ALL) {
            fetchAllComponents(profile, profileDto);
        } else if (componentFetchStrategy == ComponentFetchStrategy.PUBLIC) {
            fetchPublicComponents(profile, profileDto);
        }
    }

    private void fetchAllComponents(Profile profile, ProfileDto profileDto) {
        Arrays.stream(ProfileComponent.values()).forEach(componentType ->
            fetchComponentData(profile.id, profileDto, componentType.toString(), null, ComponentFetchStrategy.ALL));
    }

    private void fetchPublicComponents(Profile profile, ProfileDto profileDto) {
        List<ComponentVisibilityDto> visibilities = componentVisibilityService.findByProfileId(profile.id);
        Map<String, List<ComponentVisibilityDto>> visibilitiesByComponentType;

        if (profile.profileRole == ProfileRole.TEACHER) {
            List<TeacherProfileSection> publicSections = getPublicTeacherProfileSections(visibilities);

            visibilitiesByComponentType = visibilities.stream()
                .filter(visibility -> isVisiblePublicTeacherProfileComponent(visibility, publicSections))
                .collect(Collectors.groupingBy(v -> v.component));
        } else {
            visibilitiesByComponentType = visibilities.stream()
                .filter(this::isPublicProfileComponent)
                .collect(Collectors.groupingBy(v -> v.component));
        }

        visibilitiesByComponentType
            .entrySet()
            .stream()
            .forEach(e -> fetchComponentData(profile.id, profileDto, e.getKey(), e.getValue(), ComponentFetchStrategy.PUBLIC));
    }

    private List<TeacherProfileSection> getPublicTeacherProfileSections(List<ComponentVisibilityDto> visibilities) {
        return visibilities.stream()
            .filter(visibility -> visibility.teacherProfileSection != null &&
                visibility.component == null &&
                ComponentVisibility.Visibility.valueOf(visibility.visibility).isPublic()
            )
            .map(visibility -> TeacherProfileSection.valueOf(visibility.teacherProfileSection))
            .collect(Collectors.toList());
    }

    private boolean isPublicProfileComponent(ComponentVisibilityDto visibility) {

        return ComponentVisibility.Visibility.valueOf(visibility.visibility).isPublic() &&
            visibility.component != null;
    }

    private boolean isInsidePublicTeacherProfileSection(ComponentVisibilityDto visibility,
                                                        List<TeacherProfileSection> publicSections) {
        return visibility.teacherProfileSection != null &&
            publicSections.contains(TeacherProfileSection.valueOf(visibility.teacherProfileSection));
    }

    private boolean isNotInsideAnyTeacherProfileSection(ComponentVisibilityDto visibility) {
        return visibility.teacherProfileSection == null;
    }

    private boolean isVisiblePublicTeacherProfileComponent(ComponentVisibilityDto visibility,
                                                           List<TeacherProfileSection> publicSections) {
        return isPublicProfileComponent(visibility) &&
            (isInsidePublicTeacherProfileSection(visibility, publicSections) ||
                isNotInsideAnyTeacherProfileSection(visibility));
    }

    private void fetchComponentData(Long profileId,
                                    ProfileDto profileDto,
                                    String componentType,
                                    List<ComponentVisibilityDto> componentVisibilities,
                                    ComponentFetchStrategy componentDataFetchStrategy) {
        final boolean fetchPublic = ComponentFetchStrategy.PUBLIC.equals(componentDataFetchStrategy);
        switch (ProfileComponent.valueOf(componentType)) {
            case LANGUAGE_PROFICIENCIES:
                profileDto.languageProficiencies = fetchPublic ?
                    languageProficiencyService.findByProfileIdAndVisibility(profileId, PUBLIC) :
                    languageProficiencyService.findByProfileId(profileId);
                break;
            case FREE_TEXT_CONTENT:
                profileDto.freeTextContent = componentVisibilities != null ?
                    freeTextContentService.findByProfileIdAndComponentVisibilities(profileId, componentVisibilities) :
                    freeTextContentService.findByProfileId(profileId);
                break;
            case WORK_EXPERIENCE:
                profileDto.workExperience = fetchPublic ?
                    workExperienceService.findByProfileIdAndVisibility(profileId, PUBLIC) :
                    workExperienceService.findByProfileId(profileId);
                profileDto.jobSearch = jobSearchService.findByProfileId(profileId);
                break;
            case SAMPLES:
                profileDto.samples = fetchPublic ?
                    sampleService.findByProfileIdAndVisibility(profileId, PUBLIC) :
                    sampleService.findByProfileId(profileId);
                break;
            case CONTACT_INFORMATION:
                profileDto.contactInformation = contactInformationService.findByProfileId(profileId);
                break;
            case DEGREES:
                profileDto.degrees = fetchPublic ?
                    degreeService.findByProfileIdAndVisibility(profileId, PUBLIC) :
                    degreeService.findByProfileId(profileId);
                break;
            case STUDIES:
                profileDto.keywords = keywordRelationshipService.findByProfileId(profileId);
                profileDto.summary = new SummaryDto(profileRepository.findById(profileId).orElseThrow(NotFoundException::new).summary);
                break;
            case CREDITS:
            case ATTAINMENTS:
            default:
                // Do not eagerly fetch components that involve external API calls
                break;
        }
    }

    public enum ComponentFetchStrategy {
        ALL, NONE, PUBLIC
    }
}
