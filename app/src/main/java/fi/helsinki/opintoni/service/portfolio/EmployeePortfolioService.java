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

import fi.helsinki.opintoni.dto.portfolio.ContactInformationDto;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.web.arguments.PortfolioRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EmployeePortfolioService {
    private final PortfolioService portfolioService;
    private final EmployeeContactInformationService employeeContactInformationService;

    @Autowired
    public EmployeePortfolioService(PortfolioService portfolioService,
                                    EmployeeContactInformationService employeeContactInformationService) {
        this.portfolioService = portfolioService;
        this.employeeContactInformationService = employeeContactInformationService;
    }

    public PortfolioDto insert(Long userId, AppUser appUser, Locale locale) {

        PortfolioDto portfolioDto = portfolioService.insert(userId, appUser.getCommonName(), PortfolioRole.TEACHER,
            Language.fromCode(locale.getLanguage()));

        ContactInformationDto contactInformationDto = appUser.getEmployeeNumber()
            .map(employeeNumber -> employeeContactInformationService.fetchAndSaveEmployeeContactInformation(portfolioDto.id, employeeNumber, locale))
            .orElse(null);

        portfolioDto.contactInformation = contactInformationDto;

        return portfolioDto;
    }
}
