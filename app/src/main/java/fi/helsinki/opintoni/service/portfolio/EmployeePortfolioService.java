package fi.helsinki.opintoni.service.portfolio;

import fi.helsinki.opintoni.dto.portfolio.ContactInformationDto;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
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

        PortfolioDto portfolioDto = portfolioService.insert(userId, appUser.getCommonName(), PortfolioRole.TEACHER);

        ContactInformationDto contactInformationDto = appUser.getEmployeeNumber()
            .map(employeeNumber -> employeeContactInformationService.fetchAndSaveEmployeeContactInformation(portfolioDto.id, employeeNumber, locale))
            .orElse(null);

        portfolioDto.contactInformation = contactInformationDto;

        return portfolioDto;
    }
}
