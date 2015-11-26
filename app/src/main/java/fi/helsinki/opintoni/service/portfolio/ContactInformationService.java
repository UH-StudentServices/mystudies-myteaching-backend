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
import fi.helsinki.opintoni.repository.portfolio.ContactInformationRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.repository.portfolio.SomeLinkRepository;
import fi.helsinki.opintoni.service.DtoService;
import fi.helsinki.opintoni.service.converter.ContactInformationConverter;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.contactinformation
    .UpdateContactInformationWithSomeLinksRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ContactInformationService extends DtoService {

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
        return getDto(portfolioId,
            contactInformationRepository::findByPortfolioId,
            contactInformationConverter::toDto);
    }

    public ContactInformationDto updateContactInformationWithSomeLinks(
        Long portfolioId,
        UpdateContactInformationWithSomeLinksRequest request) {

        Portfolio portfolio = portfolioRepository.findOne(portfolioId);

        ContactInformation contactInformation = insertOrUpdateContactInformation(request, portfolio);
        updateSomeLinks(request, portfolio);

        return contactInformationConverter.toDto(contactInformation);
    }

    private ContactInformation insertOrUpdateContactInformation(
        UpdateContactInformationWithSomeLinksRequest request,
        Portfolio portfolio) {

        ContactInformation contactInformation = contactInformationRepository.findByPortfolioId(portfolio.id)
            .orElse(new ContactInformation());
        contactInformation.portfolio = portfolio;
        contactInformation.email = request.email;
        contactInformation.phoneNumber = request.phoneNumber;
        return contactInformationRepository.save(contactInformation);
    }

    private void updateSomeLinks(UpdateContactInformationWithSomeLinksRequest request, Portfolio portfolio) {
        someLinkRepository.delete(someLinkRepository.findByPortfolioId(portfolio.id));
        request.someLinks.stream().forEach(link -> {
            SomeLink someLink = new SomeLink();
            someLink.portfolio = portfolio;
            someLink.type = SomeLink.Type.valueOf(link.type);
            someLink.url = link.url;
            someLinkRepository.save(someLink);
        });
    }
}
