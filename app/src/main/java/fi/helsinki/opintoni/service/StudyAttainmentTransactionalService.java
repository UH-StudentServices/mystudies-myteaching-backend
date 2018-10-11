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

import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.domain.portfolio.StudyAttainmentWhitelist;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioStudyAttainmentWhitelistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudyAttainmentTransactionalService {

    private final PortfolioStudyAttainmentWhitelistRepository whitelistRepository;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public StudyAttainmentTransactionalService(PortfolioStudyAttainmentWhitelistRepository whitelistRepository,
                                               PortfolioRepository portfolioRepository) {
        this.whitelistRepository = whitelistRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public Portfolio findPortfolio(Long portfolioId) {
        return portfolioRepository.findById(portfolioId).orElse(null);
    }

    public Optional<StudyAttainmentWhitelist> findByPortfolioId(Long portfolioId) {
        return whitelistRepository.findByPortfolioId(portfolioId);
    }
}
