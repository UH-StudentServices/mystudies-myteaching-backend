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

import fi.helsinki.opintoni.dto.portfolio.CreditsDto;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiRoles;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.CreditsConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditsService extends DtoService {

    private final OodiClient oodiClient;
    private final CreditsConverter creditsConverter;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public CreditsService(OodiClient oodiClient,
                          CreditsConverter creditsConverter, PortfolioRepository portfolioRepository) {
        this.oodiClient = oodiClient;
        this.creditsConverter = creditsConverter;
        this.portfolioRepository = portfolioRepository;
    }

    public CreditsDto getCreditsByPortfolioId(Long portfolioId) {
        String oodiPersonId = getOodiPersonIdByPortfolioId(portfolioId);

        OodiRoles oodiRoles = oodiClient.getRoles(oodiPersonId);

        return getDto(
            () -> oodiClient.getStudentInfo(oodiRoles.studentNumber),
            creditsConverter::toDto
        );
    }

    @Transactional
    private String getOodiPersonIdByPortfolioId(Long portfolioId) {
        return portfolioRepository.findOne(portfolioId).user.oodiPersonId;
    }
}
