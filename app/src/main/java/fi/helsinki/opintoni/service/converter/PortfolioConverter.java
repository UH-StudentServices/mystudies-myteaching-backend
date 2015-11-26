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

import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.dto.UserSettingsDto;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.service.AvatarImageService;
import fi.helsinki.opintoni.service.ComponentVisibilityService;
import fi.helsinki.opintoni.service.UserSettingsService;
import fi.helsinki.opintoni.util.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PortfolioConverter {

    private final UriBuilder uriBuilder;
    private final ComponentVisibilityService componentVisibilityService;
    private final UserSettingsService userSettingsService;
    private final AvatarImageService avatarImageService;

    @Autowired
    public PortfolioConverter(UriBuilder uriBuilder,
                              ComponentVisibilityService componentVisibilityService,
                              UserSettingsService userSettingsService,
                              AvatarImageService avatarImageService) {
        this.uriBuilder = uriBuilder;
        this.componentVisibilityService = componentVisibilityService;
        this.userSettingsService = userSettingsService;
        this.avatarImageService = avatarImageService;
    }

    public PortfolioDto toDto(Portfolio portfolio) {
        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.id = portfolio.id;
        portfolioDto.url = uriBuilder.getPortfolioUrl(portfolio);
        portfolioDto.intro = portfolio.intro;
        portfolioDto.ownerName = portfolio.ownerName;
        portfolioDto.backgroundUri = getBackgroundUri(portfolio);
        portfolioDto.visibility = portfolio.visibility;
        portfolioDto.avatarUrl = avatarImageService.getPortfolioAvatarImageUrl(portfolio.getOwnerId());
        portfolioDto.componentVisibilities = componentVisibilityService.findByPortfolioId(portfolio.id);
        return portfolioDto;
    }

    private String getBackgroundUri(Portfolio portfolio) {
        UserSettingsDto userSettingsDto = userSettingsService.findByUserId(portfolio.user.id);
        return userSettingsDto.backgroundUri;
    }

}
