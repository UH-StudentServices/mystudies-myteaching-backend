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
import fi.helsinki.opintoni.dto.portfolio.CreditsDto;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiRoles;
import fi.helsinki.opintoni.service.converter.CreditsConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditsService extends DtoService {

    private final OodiClient oodiClient;
    private final CreditsConverter creditsConverter;
    private final CreditsTransactionalService creditsTransactionalService;

    @Autowired
    public CreditsService(OodiClient oodiClient,
                          CreditsConverter creditsConverter, CreditsTransactionalService creditsTransactionalService) {
        this.oodiClient = oodiClient;
        this.creditsConverter = creditsConverter;
        this.creditsTransactionalService = creditsTransactionalService;
    }

    public CreditsDto getCreditsByPortfolioId(Long portfolioId) {
        Portfolio portfolio = creditsTransactionalService.findPortfolio(portfolioId);
        String oodiPersonId = portfolio.user.oodiPersonId;

        OodiRoles oodiRoles = oodiClient.getRoles(oodiPersonId);

        return getDto(
            () -> oodiClient.getStudentInfo(oodiRoles.studentNumber),
            creditsConverter::toDto
        );
    }
}
