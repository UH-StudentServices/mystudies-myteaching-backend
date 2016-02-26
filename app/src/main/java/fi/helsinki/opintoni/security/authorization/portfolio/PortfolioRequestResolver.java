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

package fi.helsinki.opintoni.security.authorization.portfolio;

import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.service.converter.PortfolioConverter;
import fi.helsinki.opintoni.service.portfolio.PortfolioService;
import fi.helsinki.opintoni.web.arguments.PortfolioRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@Component
public class PortfolioRequestResolver {

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioRequestResolver(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    public Optional<PortfolioDto> resolve(HttpServletRequest request) {
        Map<String, String> templateVariables = getTemplateVariables(request);

        if (templateVariables.containsKey("path")) {
            return getPortfolioDtoByPath(templateVariables);
        } else if (templateVariables.containsKey("portfolioId")) {
            return getPortfolioDtoById(templateVariables);
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getTemplateVariables(HttpServletRequest request) {
        return (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    private Optional<PortfolioDto> getPortfolioDtoById(Map<String, String> templateVariables) {
        return Optional.ofNullable(portfolioService.findById(Long.valueOf(templateVariables.get("portfolioId"))));
    }

    private Optional<PortfolioDto> getPortfolioDtoByPath(Map<String, String> templateVariables) {
        return Optional.ofNullable(portfolioService
            .findByPath(
                templateVariables.get("path"),
                PortfolioRole.fromValue(templateVariables.get("portfolioRole")), PortfolioConverter.ComponentFetchStrategy.NONE));
    }

}
