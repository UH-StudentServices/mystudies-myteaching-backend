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

package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.domain.portfolio.ComponentVisibility;
import fi.helsinki.opintoni.dto.portfolio.ComponentVisibilityDto;
import fi.helsinki.opintoni.repository.ComponentVisibilityRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.portfolio.ComponentVisibilityConverter;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.componentvisibility.UpdateComponentVisibilityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ComponentVisibilityService extends DtoService {

    private final ComponentVisibilityRepository componentVisibilityRepository;
    private final ComponentVisibilityConverter componentVisibilityConverter;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public ComponentVisibilityService(ComponentVisibilityRepository componentVisibilityRepository,
                                      ComponentVisibilityConverter componentVisibilityConverter,
                                      PortfolioRepository portfolioRepository) {
        this.componentVisibilityRepository = componentVisibilityRepository;
        this.componentVisibilityConverter = componentVisibilityConverter;
        this.portfolioRepository = portfolioRepository;
    }

    public List<ComponentVisibilityDto> findByPortfolioId(Long portfolioId) {
        return getDtos(portfolioId,
            componentVisibilityRepository::findByPortfolioId,
            componentVisibilityConverter::toDto);
    }

    public void update(Long portfolioId, UpdateComponentVisibilityRequest request) {
        ComponentVisibility componentVisibility = componentVisibilityRepository
            .findByPortfolioIdAndComponentAndTeacherPortfolioSectionAndInstanceName(portfolioId, request.component,
                request.teacherPortfolioSection, request.instanceName)
            .orElse(new ComponentVisibility());

        componentVisibility.component = request.component;
        componentVisibility.teacherPortfolioSection = request.teacherPortfolioSection;
        componentVisibility.instanceName = request.instanceName;
        componentVisibility.visibility = request.visibility;
        componentVisibility.portfolio = portfolioRepository.findOne(portfolioId);

        componentVisibilityRepository.save(componentVisibility);
    }

    public void save(List<ComponentVisibility> visibilities) {
        componentVisibilityRepository.save(visibilities);
    }
}
