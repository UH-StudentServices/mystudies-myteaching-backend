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

package fi.helsinki.opintoni.service.profile;

import fi.helsinki.opintoni.domain.profile.*;
import fi.helsinki.opintoni.dto.profile.ContactInformationDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.profile.*;
import fi.helsinki.opintoni.service.converter.profile.ContactInformationConverter;
import fi.helsinki.opintoni.web.rest.privateapi.profile.contactinformation.UpdateContactInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ContactInformationService {

    private final ContactInformationRepository contactInformationRepository;
    private final ContactInformationConverter contactInformationConverter;
    private final ProfileRepository profileRepository;
    private final SomeLinkRepository someLinkRepository;

    @Autowired
    public ContactInformationService(ContactInformationRepository contactInformationRepository,
                                     ContactInformationConverter contactInformationConverter,
                                     ProfileRepository profileRepository,
                                     SomeLinkRepository someLinkRepository) {
        this.contactInformationRepository = contactInformationRepository;
        this.contactInformationConverter = contactInformationConverter;
        this.profileRepository = profileRepository;
        this.someLinkRepository = someLinkRepository;
    }

    public ContactInformationDto findByProfileId(Long profileId) {
        return contactInformationRepository.findByProfileId(profileId)
            .map(contactInformationConverter::toDto)
            .orElse(null);
    }

    public ContactInformationDto updateContactInformationWithSomeLinks(Long profileId,
                                                                       UpdateContactInformation request) {

        Profile profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);

        ContactInformation contactInformation = insertOrUpdateContactInformation(profile, request);
        updateSomeLinks(profile, request);

        return contactInformationConverter.toDto(contactInformation);
    }

    public ContactInformationDto updateContactInformation(Long profileId,
                                                          UpdateContactInformation request) {

        Profile profile = profileRepository.findById(profileId).orElseThrow(NotFoundException::new);

        ContactInformation contactInformation = insertOrUpdateContactInformation(profile, request);

        return contactInformationConverter.toDto(contactInformation);
    }

    private ContactInformation insertOrUpdateContactInformation(
        Profile profile,
        UpdateContactInformation request) {

        ContactInformation contactInformation = contactInformationRepository.findByProfileId(profile.id)
            .orElse(new ContactInformation());
        contactInformation.profile = profile;
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

    private void updateSomeLinks(Profile profile, UpdateContactInformation request) {
        someLinkRepository.deleteAll(someLinkRepository.findByProfileId(profile.id));
        request.someLinks.forEach(link -> {
            SomeLink someLink = new SomeLink();
            someLink.profile = profile;
            someLink.type = SomeLink.Type.valueOf(link.type);
            someLink.url = link.url;
            someLinkRepository.save(someLink);
        });
    }
}
