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

package fi.helsinki.opintoni.service.converter;

import fi.helsinki.opintoni.domain.portfolio.ContactInformation;
import fi.helsinki.opintoni.dto.portfolio.ContactInformationDto;
import fi.helsinki.opintoni.service.portfolio.SomeLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContactInformationConverter {

    private final SomeLinkService someLinkService;

    @Autowired
    public ContactInformationConverter(SomeLinkService someLinkService) {
        this.someLinkService = someLinkService;
    }

    public ContactInformationDto toDto(ContactInformation contactInformation, long portfolioId) {
        ContactInformationDto contactInformationDto = new ContactInformationDto();
        contactInformationDto.email = contactInformation.email;
        contactInformationDto.phoneNumber = contactInformation.phoneNumber;
        contactInformationDto.workNumber = contactInformation.workNumber;
        contactInformationDto.workMobile = contactInformation.workMobile;
        contactInformationDto.title = contactInformation.title;
        contactInformationDto.faculty = contactInformation.faculty;
        contactInformationDto.financialUnit = contactInformation.financialUnit;
        contactInformationDto.workAddress = contactInformation.workAddress;
        contactInformationDto.workPostcode = contactInformation.workPostcode;

        contactInformationDto.someLinks = someLinkService.findByPortfolioId(portfolioId);
        return contactInformationDto;
    }

    public ContactInformationDto toDto(ContactInformation contactInformation) {
        return toDto(contactInformation, contactInformation.portfolio.id);
    }
}
