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

import fi.helsinki.opintoni.domain.portfolio.ComponentVisibility;
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.domain.portfolio.TeacherPortfolioSection;
import fi.helsinki.opintoni.dto.UserSettingsDto;
import fi.helsinki.opintoni.dto.portfolio.ComponentVisibilityDto;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.dto.portfolio.SummaryDto;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.AvatarImageService;
import fi.helsinki.opintoni.service.ComponentVisibilityService;
import fi.helsinki.opintoni.service.CreditsService;
import fi.helsinki.opintoni.service.UserSettingsService;
import fi.helsinki.opintoni.service.portfolio.ContactInformationService;
import fi.helsinki.opintoni.service.portfolio.DegreeService;
import fi.helsinki.opintoni.service.portfolio.FreeTextContentService;
import fi.helsinki.opintoni.service.portfolio.JobSearchService;
import fi.helsinki.opintoni.service.portfolio.LanguageProficiencyService;
import fi.helsinki.opintoni.service.portfolio.PortfolioFavoriteService;
import fi.helsinki.opintoni.service.portfolio.PortfolioKeywordRelationshipService;
import fi.helsinki.opintoni.service.portfolio.WorkExperienceService;
import fi.helsinki.opintoni.util.UriBuilder;
import fi.helsinki.opintoni.web.arguments.PortfolioRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PortfolioConverter {

    private final UriBuilder uriBuilder;
    private final ComponentVisibilityService componentVisibilityService;
    private final UserSettingsService userSettingsService;
    private final AvatarImageService avatarImageService;
    private final LanguageProficiencyService languageProficiencyService;
    private final FreeTextContentService freeTextContentService;
    private final CreditsService creditsService;
    private final PortfolioFavoriteService favoriteService;
    private final WorkExperienceService workExperienceService;
    private final JobSearchService jobSearchService;
    private final ContactInformationService contactInformationService;
    private final DegreeService degreeService;
    private final PortfolioKeywordRelationshipService keywordRelationshipService;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioConverter(UriBuilder uriBuilder,
                              ComponentVisibilityService componentVisibilityService,
                              UserSettingsService userSettingsService,
                              AvatarImageService avatarImageService,
                              LanguageProficiencyService languageProficiencyService,
                              FreeTextContentService freeTextContentService,
                              CreditsService creditsService,
                              PortfolioFavoriteService favoriteService,
                              WorkExperienceService workExperienceService,
                              JobSearchService jobSearchService,
                              ContactInformationService contactInformationService,
                              DegreeService degreeService,
                              PortfolioKeywordRelationshipService keywordRelationshipService,
                              PortfolioRepository portfolioRepository) {
        this.uriBuilder = uriBuilder;
        this.componentVisibilityService = componentVisibilityService;
        this.userSettingsService = userSettingsService;
        this.avatarImageService = avatarImageService;
        this.languageProficiencyService = languageProficiencyService;
        this.freeTextContentService = freeTextContentService;
        this.creditsService = creditsService;
        this.favoriteService = favoriteService;
        this.workExperienceService = workExperienceService;
        this.jobSearchService = jobSearchService;
        this.contactInformationService = contactInformationService;
        this.degreeService = degreeService;
        this.keywordRelationshipService = keywordRelationshipService;
        this.portfolioRepository = portfolioRepository;
    }

    public PortfolioDto toDto(Portfolio portfolio, ComponentFetchStrategy componentFetchStrategy) {
        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.id = portfolio.id;
        portfolioDto.url = uriBuilder.getPortfolioUrl(portfolio);
        portfolioDto.intro = portfolio.intro;
        portfolioDto.ownerName = portfolio.ownerName;
        portfolioDto.backgroundUri = getBackgroundUri(portfolio);
        portfolioDto.visibility = portfolio.visibility;
        portfolioDto.avatarUrl = avatarImageService.getPortfolioAvatarImageUrl(portfolio.getOwnerId());
        portfolioDto.componentVisibilities = componentVisibilityService.findByPortfolioId(portfolio.id);

        fetchPortfolioComponents(portfolio, portfolioDto, componentFetchStrategy);

        return portfolioDto;
    }

    private void fetchPortfolioComponents(Portfolio portfolio,
                                          PortfolioDto portfolioDto,
                                          ComponentFetchStrategy componentFetchStrategy) {
        if(componentFetchStrategy == ComponentFetchStrategy.ALL) {
            fetchAllComponents(portfolio, portfolioDto);
        } else if(componentFetchStrategy == ComponentFetchStrategy.PUBLIC) {
            fetchPublicComponents(portfolio, portfolioDto);
        }
    }

    private void fetchAllComponents(Portfolio portfolio, PortfolioDto portfolioDto) {
        Arrays.asList(PortfolioComponent.values()).stream().forEach(componentType -> {
            fetchComponentData(portfolio.id, portfolioDto, componentType);
        });
    }

    private void fetchPublicComponents(Portfolio portfolio, PortfolioDto portfolioDto) {
        if(portfolio.portfolioRole == PortfolioRole.TEACHER) {
            fetchPublicComponentsForTeacherPortfolio(portfolio.id, portfolioDto);
        } else {
            fetchPublicComponentsForStudentPortfolio(portfolio.id, portfolioDto);
        }
    }

    private void fetchPublicComponentsForStudentPortfolio(Long portfolioId, PortfolioDto portfolioDto) {
        componentVisibilityService.findByPortfolioId(portfolioId).stream()
            .filter(visibility -> {
                return ComponentVisibility.Visibility.valueOf(visibility.visibility).isPublic() &&
                    visibility.component != null;
            })
            .map(visibility -> PortfolioComponent.valueOf(visibility.component))
            .forEach(component -> fetchComponentData(portfolioId, portfolioDto, component));;
    }

    private void fetchPublicComponentsForTeacherPortfolio(Long portfolioId, PortfolioDto portfolioDto) {
        List<ComponentVisibilityDto> visibilities = componentVisibilityService.findByPortfolioId(portfolioId);
        List<TeacherPortfolioSection> publicSections = visibilities.stream()
            .filter(visibility -> {
                return visibility.teacherPortfolioSection != null &&
                    visibility.component == null &&
                    ComponentVisibility.Visibility.valueOf(visibility.visibility).isPublic();
            })
            .map(visibility -> TeacherPortfolioSection.valueOf(visibility.teacherPortfolioSection))
            .collect(Collectors.toList());

        visibilities.stream()
            .filter(visibility -> {
                return ComponentVisibility.Visibility.valueOf(visibility.visibility).isPublic() &&
                    visibility.component != null &&
                    publicSections.contains(TeacherPortfolioSection.valueOf(visibility.teacherPortfolioSection));
            })
            .map(visibility -> PortfolioComponent.valueOf(visibility.component))
            .forEach(component -> fetchComponentData(portfolioId, portfolioDto, component));
    }

    private void fetchComponentData(Long portfolioId, PortfolioDto portfolioDto, PortfolioComponent component) {
        switch(component) {
            case LANGUAGE_PROFICIENCIES:
                portfolioDto.languageProficiencies = languageProficiencyService.findByPortfolioId(portfolioId);
                break;
            case FREE_TEXT_CONTENT:
                portfolioDto.freeTextContent = freeTextContentService.findByPortfolioId(portfolioId);
                break;
            case FAVORITES:
                portfolioDto.favorites = favoriteService.findByPortfolioId(portfolioId);
                break;
            case WORK_EXPERIENCE:
                portfolioDto.workExperience = workExperienceService.findByPortfolioId(portfolioId);
                portfolioDto.jobSearch = jobSearchService.findByPortfolioId(portfolioId);
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

    private String getBackgroundUri(Portfolio portfolio) {
        UserSettingsDto userSettingsDto = userSettingsService.findByUserId(portfolio.user.id);
        return userSettingsDto.backgroundUri;
    }

    public enum ComponentFetchStrategy {
        ALL, NONE, PUBLIC
    }
}
