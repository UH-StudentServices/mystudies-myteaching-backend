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

package fi.helsinki.opintoni.service.converter;

import fi.helsinki.opintoni.domain.portfolio.*;
import fi.helsinki.opintoni.dto.portfolio.ComponentVisibilityDto;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.dto.portfolio.SummaryDto;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.AvatarImageService;
import fi.helsinki.opintoni.service.portfolio.*;
import fi.helsinki.opintoni.util.UriBuilder;
import fi.helsinki.opintoni.web.arguments.PortfolioRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PortfolioConverter {

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
    private final PortfolioKeywordRelationshipService keywordRelationshipService;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioBackgroundService portfolioBackgroundService;

    @Autowired
    public PortfolioConverter(UriBuilder uriBuilder,
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
                              PortfolioKeywordRelationshipService keywordRelationshipService,
                              PortfolioRepository portfolioRepository,
                              PortfolioBackgroundService portfolioBackgroundService) {
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
        this.portfolioRepository = portfolioRepository;
        this.portfolioBackgroundService = portfolioBackgroundService;
    }

    public PortfolioDto toDto(Portfolio portfolio, ComponentFetchStrategy componentFetchStrategy) {
        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.id = portfolio.id;
        portfolioDto.lang = portfolio.language.getCode();
        portfolioDto.url = uriBuilder.getPortfolioUrl(portfolio);
        portfolioDto.intro = portfolio.intro;
        portfolioDto.ownerName = portfolio.ownerName;
        portfolioDto.backgroundUri = portfolioBackgroundService.getPortfolioBackgroundUri(portfolio);
        portfolioDto.visibility = portfolio.visibility;
        portfolioDto.avatarUrl = avatarImageService.getPortfolioAvatarImageUrl(portfolio.getOwnerId());
        portfolioDto.componentVisibilities = componentVisibilityService.findByPortfolioId(portfolio.id);
        portfolioDto.componentOrders = componentOrderService.findByPortfolioId(portfolio.id);
        portfolioDto.headings = componentHeadingService.findByPortfolioId(portfolio.id);

        fetchPortfolioComponents(portfolio, portfolioDto, componentFetchStrategy);

        return portfolioDto;
    }

    private void fetchPortfolioComponents(Portfolio portfolio,
                                          PortfolioDto portfolioDto,
                                          ComponentFetchStrategy componentFetchStrategy) {
        if (componentFetchStrategy == ComponentFetchStrategy.ALL) {
            fetchAllComponents(portfolio, portfolioDto);
        } else if (componentFetchStrategy == ComponentFetchStrategy.PUBLIC) {
            fetchPublicComponents(portfolio, portfolioDto);
        }
    }

    private void fetchAllComponents(Portfolio portfolio, PortfolioDto portfolioDto) {
        Arrays.stream(PortfolioComponent.values()).forEach(componentType ->
            fetchComponentData(portfolio.id, portfolioDto, componentType.toString(), null));
    }

    private void fetchPublicComponents(Portfolio portfolio, PortfolioDto portfolioDto) {
        List<ComponentVisibilityDto> visibilities = componentVisibilityService.findByPortfolioId(portfolio.id);
        Map<String, List<ComponentVisibilityDto>> visibilitiesByComponentType;

        if (portfolio.portfolioRole == PortfolioRole.TEACHER) {
            List<TeacherPortfolioSection> publicSections = getPublicTeacherPortfolioSections(visibilities);

            visibilitiesByComponentType = visibilities.stream()
                .filter(visibility -> isVisiblePublicTeacherPortfolioComponent(visibility, publicSections))
                .collect(Collectors.groupingBy(v -> v.component));
        } else {
            visibilitiesByComponentType = visibilities.stream()
                .filter(this::isPublicPortfolioComponent)
                .collect(Collectors.groupingBy(v -> v.component));
        }

        visibilitiesByComponentType
            .entrySet()
            .stream()
            .forEach(e -> fetchComponentData(portfolio.id, portfolioDto, e.getKey(), e.getValue()));
    }

    private List<TeacherPortfolioSection> getPublicTeacherPortfolioSections(List<ComponentVisibilityDto> visibilities) {
        return visibilities.stream()
            .filter(visibility -> visibility.teacherPortfolioSection != null &&
                visibility.component == null &&
                ComponentVisibility.Visibility.valueOf(visibility.visibility).isPublic()
            )
            .map(visibility -> TeacherPortfolioSection.valueOf(visibility.teacherPortfolioSection))
            .collect(Collectors.toList());
    }

    private boolean isPublicPortfolioComponent(ComponentVisibilityDto visibility) {

        return ComponentVisibility.Visibility.valueOf(visibility.visibility).isPublic() &&
            visibility.component != null;
    }

    private boolean isInsidePublicTeacherPortfolioSection(ComponentVisibilityDto visibility,
                                                          List<TeacherPortfolioSection> publicSections) {
        return visibility.teacherPortfolioSection != null &&
            publicSections.contains(TeacherPortfolioSection.valueOf(visibility.teacherPortfolioSection));
    }

    private boolean isNotInsideAnyTeacherPortfolioSection(ComponentVisibilityDto visibility) {
        return visibility.teacherPortfolioSection == null;
    }

    private boolean isVisiblePublicTeacherPortfolioComponent(ComponentVisibilityDto visibility,
                                                             List<TeacherPortfolioSection> publicSections) {
        return isPublicPortfolioComponent(visibility) &&
            (isInsidePublicTeacherPortfolioSection(visibility, publicSections) ||
                isNotInsideAnyTeacherPortfolioSection(visibility));
    }

    private void fetchComponentData(Long portfolioId,
                                    PortfolioDto portfolioDto,
                                    String componentType,
                                    List<ComponentVisibilityDto> componentVisibilities) {
        switch (PortfolioComponent.valueOf(componentType)) {
            case LANGUAGE_PROFICIENCIES:
                portfolioDto.languageProficiencies = languageProficiencyService.findByPortfolioId(portfolioId);
                break;
            case FREE_TEXT_CONTENT:
                portfolioDto.freeTextContent = componentVisibilities != null ?
                    freeTextContentService.findByPortfolioIdAndComponentVisibilities(portfolioId, componentVisibilities) :
                    freeTextContentService.findByPortfolioId(portfolioId);
                break;
            case WORK_EXPERIENCE:
                portfolioDto.workExperience = workExperienceService.findByPortfolioId(portfolioId);
                portfolioDto.jobSearch = jobSearchService.findByPortfolioId(portfolioId);
                break;
            case SAMPLES:
                portfolioDto.samples = sampleService.findByPortfolioId(portfolioId);
                break;
            case CONTACT_INFORMATION:
                portfolioDto.contactInformation = contactInformationService.findByPortfolioId(portfolioId);
                break;
            case DEGREES:
                portfolioDto.degrees = degreeService.findByPortfolioId(portfolioId);
                break;
            case STUDIES:
                portfolioDto.keywords = keywordRelationshipService.findByPortfolioId(portfolioId);
                portfolioDto.summary = new SummaryDto(portfolioRepository.findOne(portfolioId).summary);
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
