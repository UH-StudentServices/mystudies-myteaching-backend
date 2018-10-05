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

package fi.helsinki.opintoni.service.portfolio;

import fi.helsinki.opintoni.domain.portfolio.ComponentHeading;
import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.dto.portfolio.ComponentHeadingDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.portfolio.ComponentHeadingRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.portfolio.ComponentHeadingConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComponentHeadingService {

    private final ComponentHeadingRepository componentHeadingRepository;
    private final PortfolioRepository portfolioRepository;
    private final ComponentHeadingConverter componentHeadingConverter;

    @Autowired
    public ComponentHeadingService(ComponentHeadingRepository componentHeadingRepository,
                                   PortfolioRepository portfolioRepository,
                                   ComponentHeadingConverter componentHeadingConverter) {
        this.componentHeadingRepository = componentHeadingRepository;
        this.portfolioRepository = portfolioRepository;
        this.componentHeadingConverter = componentHeadingConverter;
    }

    public List<ComponentHeadingDto> findByPortfolioId(Long portfolioId) {
        return componentHeadingRepository.findByPortfolioId(portfolioId).stream()
            .map(componentHeadingConverter::toDto)
            .collect(Collectors.toList());
    }

    public ComponentHeadingDto findByPortfolioIdAndComponent(Long portfolioId, PortfolioComponent component) {
        return componentHeadingRepository.findByPortfolioIdAndComponent(portfolioId, component)
            .map(componentHeadingConverter::toDto)
            .orElse(null);
    }

    public ComponentHeadingDto upsert(Long portfolioId, ComponentHeadingDto componentHeadingDto) {
        ComponentHeading componentHeading = componentHeadingRepository
            .findByPortfolioIdAndComponent(portfolioId, componentHeadingDto.component)
            .orElse(new ComponentHeading());

        componentHeading.component = componentHeadingDto.component;
        componentHeading.heading = componentHeadingDto.heading;
        componentHeading.portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new NotFoundException(""));

        return componentHeadingConverter.toDto(componentHeadingRepository.save(componentHeading));
    }

    public void delete(Long portfolioId, PortfolioComponent component) {
        componentHeadingRepository.findByPortfolioIdAndComponent(portfolioId, component)
            .map(componentHeading -> {
                componentHeadingRepository.delete(componentHeading);
                return true;
            });
    }
}
