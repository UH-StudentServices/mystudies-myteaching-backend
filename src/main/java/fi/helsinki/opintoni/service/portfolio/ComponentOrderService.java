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

import fi.helsinki.opintoni.domain.portfolio.ComponentOrder;
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.dto.portfolio.ComponentOrderDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.portfolio.ComponentOrderRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.portfolio.ComponentOrderConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class ComponentOrderService {

    private final ComponentOrderRepository componentOrderRepository;
    private final ComponentOrderConverter componentOrderConverter;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public ComponentOrderService(ComponentOrderRepository componentOrderRepository,
                                 ComponentOrderConverter componentOrderConverter,
                                 PortfolioRepository portfolioRepository) {
        this.componentOrderRepository = componentOrderRepository;
        this.componentOrderConverter = componentOrderConverter;
        this.portfolioRepository = portfolioRepository;
    }

    public List<ComponentOrderDto> findByPortfolioId(Long portfolioId) {
        return componentOrderRepository.findByPortfolioId(portfolioId).stream()
            .map(componentOrderConverter::toDto)
            .collect(toList());
    }

    public List<ComponentOrderDto> update(Long portfolioId, List<ComponentOrderDto> componentOrders) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow(() -> new NotFoundException("Portfolio not found"));

        componentOrderRepository.deleteByPortfolioId(portfolioId);
        componentOrderRepository.flush();

        return componentOrders.stream()
            .map(dto -> {
                ComponentOrder co = componentOrderRepository.save(componentOrderConverter.toEntity(portfolio, dto));

                return componentOrderConverter.toDto(co);
            })
            .collect(toList());
    }
}
