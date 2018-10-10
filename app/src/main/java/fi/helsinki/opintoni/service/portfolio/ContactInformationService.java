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

import fi.helsinki.opintoni.domain.portfolio.ContactInformation;
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.domain.portfolio.SomeLink;
import fi.helsinki.opintoni.dto.portfolio.ContactInformationDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.portfolio.ContactInformationRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.repository.portfolio.SomeLinkRepository;
import fi.helsinki.opintoni.service.converter.ContactInformationConverter;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.contactinformation.UpdateContactInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ContactInformationService {

    private final ContactInformationRepository contactInformationRepository;
    private final ContactInformationConverter contactInformationConverter;
    private final PortfolioRepository portfolioRepository;
    private final SomeLinkRepository someLinkRepository;

    @Autowired
    public ContactInformationService(ContactInformationRepository contactInformationRepository,
                                     ContactInformationConverter contactInformationConverter,
                                     PortfolioRepository portfolioRepository,
                                     SomeLinkRepository someLinkRepository) {
        this.contactInformationRepository = contactInformationRepository;
        this.contactInformationConverter = contactInformationConverter;
        this.portfolioRepository = portfolioRepository;
        this.someLinkRepository = someLinkRepository;
    }

    public ContactInformationDto findByPortfolioId(Long portfolioId) {
        return contactInformationRepository.findByPortfolioId(portfolioId)
            .map(contactInformationConverter::toDto)
            .orElse(null);
    }

    public ContactInformationDto updateContactInformationWithSomeLinks(
        Long portfolioId,
        UpdateContactInformation request) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NotFoundException::new);

        ContactInformation contactInformation = insertOrUpdateContactInformation(portfolio, request);
        updateSomeLinks(portfolio, request);

        return contactInformationConverter.toDto(contactInformation);
    }

    public ContactInformationDto updateContactInformation(
        Long portfolioId,
        UpdateContactInformation request) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NotFoundException::new);

        ContactInformation contactInformation = insertOrUpdateContactInformation(portfolio, request);

        return contactInformationConverter.toDto(contactInformation);
    }

    private ContactInformation insertOrUpdateContactInformation(
        Portfolio portfolio,
        UpdateContactInformation request) {

        ContactInformation contactInformation = contactInformationRepository.findByPortfolioId(portfolio.id)
            .orElse(new ContactInformation());
        contactInformation.portfolio = portfolio;
        contactInformation.email = request.email;
        contactInformation.phoneNumber = request.phoneNumber;
        contactInformation.workNumber = request.workNumber;
        contactInformation.workMobile = request.workMobile;
        contactInformation.title = request.title;
        contactInformation.faculty = request.faculty;
        contactInformation.financialUnit = request.financialUnit;
        contactInformation.workAddress = request.workAddress;
        contactInformation.workPostcode = request.workPostcode;

        return contactInformationRepository.save(contactInformation);
    }

    private void updateSomeLinks(Portfolio portfolio, UpdateContactInformation request) {
        someLinkRepository.deleteAll(someLinkRepository.findByPortfolioId(portfolio.id));
        request.someLinks.forEach(link -> {
            SomeLink someLink = new SomeLink();
            someLink.portfolio = portfolio;
            someLink.type = SomeLink.Type.valueOf(link.type);
            someLink.url = link.url;
            someLinkRepository.save(someLink);
        });
    }
}
