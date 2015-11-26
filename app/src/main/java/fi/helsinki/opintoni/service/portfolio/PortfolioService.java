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
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.summary.UpdateSummaryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

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

    public PortfolioDto insert(Long userId, String name) {
        if (portfolioRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("User portfolio already exists");
        }

        Portfolio portfolio = new Portfolio();
        portfolio.user = userRepository.findOne(userId);
        portfolio.path = portfolioPathGenerator.create(name);
        portfolio.ownerName = name;
        portfolio.visibility = PortfolioVisibility.PRIVATE;
        Portfolio inserted = portfolioRepository.save(portfolio);
        whitelistService.insert(inserted);
        return portfolioConverter.toDto(inserted);
    }

    public PortfolioDto get(Long userId) {
        return getPortfolio(portfolioRepository::findByUserId, userId);
    }

    public PortfolioDto findByPath(String path) {
        return getPortfolio(portfolioRepository::findByPath, path);
    }

    public PortfolioDto findById(Long portfolioId) {
        return getPortfolio(portfolioRepository::findById, portfolioId);
    }

    public <T> PortfolioDto getPortfolio(Function<T, Optional<Portfolio>> getter, T identifier) {
        return getter.apply(identifier)
            .map(portfolioConverter::toDto)
            .orElseThrow(notFoundException("Portfolio not found"));
    }

    public Optional<String> getUserPortfolioPath(Long userId) {
        return portfolioRepository.findByUserId(userId).map(p -> p.path);
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
