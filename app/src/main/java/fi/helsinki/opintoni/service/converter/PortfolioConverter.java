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
import fi.helsinki.opintoni.dto.UserSettingsDto;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.service.AvatarImageService;
import fi.helsinki.opintoni.service.ComponentVisibilityService;
import fi.helsinki.opintoni.service.UserSettingsService;
import fi.helsinki.opintoni.service.portfolio.LanguageProficiencyService;
import fi.helsinki.opintoni.util.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PortfolioConverter {

    private final UriBuilder uriBuilder;
    private final ComponentVisibilityService componentVisibilityService;
    private final UserSettingsService userSettingsService;
    private final AvatarImageService avatarImageService;
    private final LanguageProficiencyService languageProficiencyService;

    @Autowired
    public PortfolioConverter(UriBuilder uriBuilder,
                              ComponentVisibilityService componentVisibilityService,
                              UserSettingsService userSettingsService,
                              AvatarImageService avatarImageService,
                              LanguageProficiencyService languageProficiencyService) {
        this.uriBuilder = uriBuilder;
        this.componentVisibilityService = componentVisibilityService;
        this.userSettingsService = userSettingsService;
        this.avatarImageService = avatarImageService;
        this.languageProficiencyService = languageProficiencyService;
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

        fetchPortfolioComponents(portfolio.id, portfolioDto, componentFetchStrategy);

        return portfolioDto;
    }

    private void fetchPortfolioComponents(Long portfolioId,
                                          PortfolioDto portfolioDto,
                                          ComponentFetchStrategy componentFetchStrategy) {
        if(componentFetchStrategy == ComponentFetchStrategy.ALL) {
            fetchAllComponents(portfolioId, portfolioDto);
        } else if(componentFetchStrategy == ComponentFetchStrategy.PUBLIC) {
            fetchPublicComponents(portfolioId, portfolioDto);
        }
    }

    private void fetchPublicComponents(Long portfolioId, PortfolioDto portfolioDto) {
        componentVisibilityService.findByPortfolioId(portfolioId).stream()
            .filter(visibility -> ComponentVisibility.Visibility.PUBLIC.toString().equals(visibility.visibility))
            .map(visibility -> PortfolioComponent.valueOf(visibility.component))
            .forEach(component -> fetchComponentData(portfolioId, portfolioDto, component));
    }

    private void fetchComponentData(Long portfolioId, PortfolioDto portfolioDto, PortfolioComponent component) {
        switch(component) {
            case LANGUAGE_PROFICIENCY:
                portfolioDto.languageProficiencies = languageProficiencyService.findByPortfolioId(portfolioId);
                break;
        }
    }

    private void fetchAllComponents(Long portfolioId, PortfolioDto portfolioDto) {
        portfolioDto.languageProficiencies = languageProficiencyService.findByPortfolioId(portfolioId);
    }

    private String getBackgroundUri(Portfolio portfolio) {
        UserSettingsDto userSettingsDto = userSettingsService.findByUserId(portfolio.user.id);
        return userSettingsDto.backgroundUri;
    }

    public enum ComponentFetchStrategy {
        ALL, NONE, PUBLIC
    }
}
