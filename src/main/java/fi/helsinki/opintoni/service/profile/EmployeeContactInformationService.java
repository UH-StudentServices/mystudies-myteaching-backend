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

import fi.helsinki.opintoni.domain.profile.ContactInformation;
import fi.helsinki.opintoni.dto.profile.ContactInformationDto;
import fi.helsinki.opintoni.integration.esb.*;
import fi.helsinki.opintoni.service.converter.LocalizedValueConverter;
import fi.helsinki.opintoni.service.converter.profile.ContactInformationConverter;
import fi.helsinki.opintoni.web.rest.privateapi.profile.contactinformation.UpdateContactInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeeContactInformationService {

    public static final String FINANCIAL_UNIT_TYPE = "FINANCIAL_UNIT";
    public static final String FACULTY_TYPE = "FACULTY";
    public static final String PRIMARY_RECORD_TYPE = "Y";
    public static final String EMPLOYEE_NUMBER_PREFIX = "9";

    private final ESBClient esbClient;
    private final ContactInformationConverter contactInformationConverter;
    private final LocalizedValueConverter localizedValueConverter;
    private final ContactInformationService contactInformationService;

    @Autowired
    public EmployeeContactInformationService(ESBClient esbClient,
                                             ContactInformationConverter contactInformationConverter,
                                             LocalizedValueConverter localizedValueConverter,
                                             ContactInformationService contactInformationService) {
        this.esbClient = esbClient;
        this.contactInformationConverter = contactInformationConverter;
        this.localizedValueConverter = localizedValueConverter;
        this.contactInformationService = contactInformationService;
    }

    public ContactInformationDto fetchAndSaveEmployeeContactInformation(long profileId, String employeeNumber, Locale locale) {
        ContactInformation contactInformation = fetchEmployeeContactInformation(employeeNumber, locale);
        return contactInformationService.updateContactInformation(profileId, new UpdateContactInformation(contactInformation));
    }

    public ContactInformationDto fetchEmployeeContactInformation(long profileId, String employeeNumber, Locale locale) {
        ContactInformation contactInformation = fetchEmployeeContactInformation(employeeNumber, locale);
        return contactInformationConverter.toDto(contactInformation, profileId);
    }

    private ContactInformation fetchEmployeeContactInformation(String employeeNumber, Locale locale) {
        Optional<ESBEmployeeInfo> esbEmployeeInfoOptional = esbClient
            .getEmployeeInfo(getPrefixedEmployeeNumber(employeeNumber)).stream()
            .findFirst();

        return esbEmployeeInfoOptional
            .map(esbEmployeeInfo -> getContactInformation(esbEmployeeInfo, locale))
            .orElseGet(ContactInformation::new);
    }

    private String getPrefixedEmployeeNumber(String employeeNumber) {
        return EMPLOYEE_NUMBER_PREFIX + employeeNumber;
    }

    private ContactInformation getContactInformation(ESBEmployeeInfo esbEmployeeInfo, Locale locale) {
        return findPrimaryOrFirstRecord(esbEmployeeInfo.records)
            .map(record -> {
                ContactInformation contactInformation = new ContactInformation();

                contactInformation.email = record.email;
                contactInformation.workNumber = record.workNumber;
                contactInformation.workMobile = record.workMobile;
                contactInformation.title = record.title;
                contactInformation.faculty = findOrganizationNameByType(record.ocOrganisations, FACULTY_TYPE, locale);
                contactInformation.financialUnit = findOrganizationNameByType(record.hrOrganisations, FINANCIAL_UNIT_TYPE, locale);
                contactInformation.workAddress = record.workAddress;
                contactInformation.workPostcode = record.workPostcode;
                return contactInformation;
            })
            .orElseGet(ContactInformation::new);
    }

    private String findOrganizationNameByType(List<ESBEmployeeInfoOrganization> organizations, String type, Locale locale) {
        return organizations.stream()
            .filter(organization -> type.equals(organization.type))
            .findFirst()
            .map(organization -> localizedValueConverter.toLocalizedString(organization.name, locale))
            .orElse(null);
    }

    private Optional<ESBEmployeeInfoRecord> findPrimaryOrFirstRecord(List<ESBEmployeeInfoRecord> records) {
        Optional<ESBEmployeeInfoRecord> primaryRecord = records
            .stream()
            .filter(record -> PRIMARY_RECORD_TYPE.equals(record.recordType))
            .findFirst();

        return primaryRecord.isPresent() ? primaryRecord : records.stream().findFirst();
    }
}
