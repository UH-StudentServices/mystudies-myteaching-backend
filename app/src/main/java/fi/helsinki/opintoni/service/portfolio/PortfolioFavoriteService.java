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
import fi.helsinki.opintoni.dto.FavoriteDto;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static fi.helsinki.opintoni.exception.http.NotFoundException.notFoundException;

@Service
@Transactional
public class PortfolioFavoriteService {

    private final PortfolioRepository portfolioRepository;
    private final FavoriteService favoriteService;

    @Autowired
    public PortfolioFavoriteService(FavoriteService favoriteService, PortfolioRepository portfolioRepository) {
        this.favoriteService = favoriteService;
        this.portfolioRepository = portfolioRepository;
    }

    public List<FavoriteDto> findByPortfolioId(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
            .map(this::findFavoritesForPortfolio)
            .orElseThrow(notFoundException("Portfolio not found"));
    }

    private List<FavoriteDto> findFavoritesForPortfolio(Portfolio portfolio) {
        return favoriteService.findByUserIdForPortfolio(portfolio.getOwnerId());
    }
}
