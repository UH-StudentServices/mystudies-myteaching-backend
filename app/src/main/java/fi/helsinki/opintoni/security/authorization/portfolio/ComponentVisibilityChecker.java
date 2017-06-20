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

import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.repository.portfolio.ComponentVisibilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ComponentVisibilityChecker {

    private final ComponentVisibilityRepository componentVisibilityRepository;

    @Autowired
    public ComponentVisibilityChecker(ComponentVisibilityRepository componentVisibilityRepository) {
        this.componentVisibilityRepository = componentVisibilityRepository;
    }

    public boolean isPublic(Long portfolioId, PortfolioComponent component) {
        return componentVisibilityRepository.findByPortfolioIdAndComponent(portfolioId, component)
            .map(componentVisibility -> componentVisibility.visibility.isPublic())
            .orElse(false);
    }
}
