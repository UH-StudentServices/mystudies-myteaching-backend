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

import fi.helsinki.opintoni.domain.portfolio.PortfolioBackground;
import fi.helsinki.opintoni.repository.portfolio.PortfolioBackgroundRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.SelectBackgroundRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class PortfolioBackgroundService {

    private final PortfolioBackgroundRepository portfolioBackgroundRepository;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioBackgroundService(PortfolioBackgroundRepository portfolioBackgroundRepository,
                                      PortfolioRepository portfolioRepository) {
        this.portfolioBackgroundRepository = portfolioBackgroundRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public void selectBackground(Long id, SelectBackgroundRequest request) {
        Optional<PortfolioBackground> portfolioBackgroundOptional = portfolioBackgroundRepository.findByPortfolioId(id);
        PortfolioBackground portfolioBackground;

        if (!portfolioBackgroundOptional.isPresent()) {
            portfolioBackground = new PortfolioBackground();
            portfolioBackground.portfolio = portfolioRepository.findOne(id);
        } else {
            portfolioBackground = portfolioBackgroundOptional.get();
        }

        portfolioBackground.backgroundFilename = request.filename;

        portfolioBackgroundRepository.save(portfolioBackground);
    }
}
