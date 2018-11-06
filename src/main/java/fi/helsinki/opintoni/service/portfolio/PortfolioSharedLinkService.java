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
import fi.helsinki.opintoni.domain.portfolio.PortfolioSharedLink;
import fi.helsinki.opintoni.dto.portfolio.PortfolioSharedLinkDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioSharedLinkRepository;
import fi.helsinki.opintoni.security.authorization.PermissionChecker;
import fi.helsinki.opintoni.service.converter.portfolio.PortfolioSharedLinkConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class PortfolioSharedLinkService {

    private final PortfolioSharedLinkRepository portfolioSharedLinkRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioSharedLinkConverter portfolioSharedLinkConverter;
    private final PermissionChecker permissionChecker;

    @Autowired
    public PortfolioSharedLinkService(PortfolioSharedLinkRepository portfolioSharedLinkRepository,
                                      PortfolioRepository portfolioRepository,
                                      PortfolioSharedLinkConverter portfolioSharedLinkConverter,
                                      PermissionChecker permissionChecker) {
        this.portfolioSharedLinkRepository = portfolioSharedLinkRepository;
        this.portfolioSharedLinkConverter = portfolioSharedLinkConverter;
        this.portfolioRepository = portfolioRepository;
        this.permissionChecker = permissionChecker;
    }

    public PortfolioSharedLinkDto createSharedLink(final Long portfolioId, final Long userId, final PortfolioSharedLinkDto sharedLinkDto) {
        permissionChecker.verifyPermission(userId, portfolioId, Portfolio.class);
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NotFoundException::new);
        PortfolioSharedLink sharedLink = new PortfolioSharedLink();
        sharedLink.portfolio = portfolio;
        sharedLink.sharedPathFragment = UUID.randomUUID().toString();
        sharedLink.expiryDate = sharedLinkDto.expiryDate;

        return portfolioSharedLinkConverter.toDto(portfolioSharedLinkRepository.save(sharedLink));
    }

    public List<PortfolioSharedLinkDto> getSharedLinks(final Long portfolioId, final Long userId) {
        permissionChecker.verifyPermission(userId, portfolioId, Portfolio.class);
        return portfolioSharedLinkRepository.findByPortfolioId(portfolioId).stream()
            .map(portfolioSharedLinkConverter::toDto)
            .collect(toList());
    }

    public void deleteSharedLink(final Long portfolioId, final Long userId, final Long sharedLinkId) {
        permissionChecker.verifyPermission(userId, portfolioId, Portfolio.class);
        portfolioSharedLinkRepository.deleteById(sharedLinkId);
    }
}
