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

import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.domain.portfolio.PortfolioVisibility;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.dto.portfolio.SummaryDto;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.PortfolioConverter;
import fi.helsinki.opintoni.web.arguments.PortfolioRole;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.summary.UpdateSummaryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fi.helsinki.opintoni.exception.http.NotFoundException.notFoundException;


@Service
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final PortfolioPathGenerator portfolioPathGenerator;
    private final PortfolioConverter portfolioConverter;
    private final PortfolioStudyAttainmentWhitelistService whitelistService;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository,
                            UserRepository userRepository,
                            PortfolioPathGenerator portfolioPathGenerator,
                            PortfolioConverter portfolioConverter,
                            PortfolioStudyAttainmentWhitelistService whitelistService) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.portfolioPathGenerator = portfolioPathGenerator;
        this.portfolioConverter = portfolioConverter;
        this.whitelistService = whitelistService;
    }

    public PortfolioDto insert(Long userId, String name, PortfolioRole portfolioRole) {
        String portfolioPath = portfolioRepository
            .findByUserId(userId)
            .findFirst()
            .map(portfolio -> portfolio.path)
            .orElse(portfolioPathGenerator.create(name));

        Portfolio portfolio = new Portfolio();
        portfolio.user = userRepository.findOne(userId);
        portfolio.path = portfolioPath;
        portfolio.ownerName = name;
        portfolio.visibility = PortfolioVisibility.PRIVATE;
        portfolio.portfolioRole = portfolioRole;
        Portfolio inserted = portfolioRepository.save(portfolio);
        whitelistService.insert(inserted);
        return portfolioConverter.toDto(inserted);
    }

    public PortfolioDto get(Long userId, PortfolioRole portfolioRole) {
        return convertPortfolioToDto(portfolioRepository
            .findByUserIdAndPortfolioRole(userId, portfolioRole));
    }

    public PortfolioDto findByPath(String path, PortfolioRole portfolioRole) {
        return convertPortfolioToDto(portfolioRepository
            .findByPathAndPortfolioRole(path, portfolioRole));
    }

    public PortfolioDto findById(Long portfolioId) {
        return convertPortfolioToDto(portfolioRepository
            .findById(portfolioId));
    }

    private PortfolioDto convertPortfolioToDto(Optional<Portfolio> portfolioOptional) {
        return portfolioOptional
            .map(portfolioConverter::toDto)
            .orElseThrow(notFoundException("Portfolio not found"));
    }

    public Map<String, String> getUserPortfolioPath(Long userId) {
        return portfolioRepository
            .findByUserId(userId)
            .collect(Collectors.toMap(
                portfolio -> portfolio.portfolioRole.getRole(),
                portfolio -> portfolio.path));
    }

    public PortfolioDto update(Long portfolioId, PortfolioDto portfolioDto) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);
        portfolio.visibility = portfolioDto.visibility;
        portfolio.ownerName = portfolioDto.ownerName;
        portfolio.intro = portfolioDto.intro;
        return portfolioConverter.toDto(portfolioRepository.save(portfolio));
    }

    public SummaryDto getSummary(Long portfolioId) {
        return new SummaryDto(portfolioRepository.findOne(portfolioId).summary);
    }

    public void updateSummary(Long portfolioId, UpdateSummaryRequest request) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);
        portfolio.summary = request.summary;
        portfolioRepository.save(portfolio);
    }
}
