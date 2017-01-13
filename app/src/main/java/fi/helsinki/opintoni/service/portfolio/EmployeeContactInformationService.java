package fi.helsinki.opintoni.service.portfolio;

import fi.helsinki.opintoni.domain.portfolio.ContactInformation;
import fi.helsinki.opintoni.dto.portfolio.ContactInformationDto;
import fi.helsinki.opintoni.integration.esb.ESBClient;
import fi.helsinki.opintoni.integration.esb.ESBEmployeeInfo;
import fi.helsinki.opintoni.integration.esb.ESBEmployeeInfoOrganization;
import fi.helsinki.opintoni.integration.esb.ESBEmployeeInfoRecord;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.ContactInformationConverter;
import fi.helsinki.opintoni.service.converter.LocalizedValueConverter;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.contactinformation.UpdateContactInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class EmployeeContactInformationService {
    public static final String FINANCIAL_UNIT_TYPE = "FINANCIAL_UNIT";
    public static final String FACULTY_TYPE = "FACULTY";
    public static final String PRIMARY_RECORD_TYPE = "Y";

    private final ESBClient esbClient;
    private final ContactInformationConverter contactInformationConverter;
    private final LocalizedValueConverter localizedValueConverter;
    private final ContactInformationService contactInformationService;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public EmployeeContactInformationService(ESBClient esbClient,
                                             ContactInformationConverter contactInformationConverter,
                                             LocalizedValueConverter localizedValueConverter,
                                             ContactInformationService contactInformationService,
                                             PortfolioRepository portfolioRepository) {
        this.esbClient = esbClient;
        this.contactInformationConverter = contactInformationConverter;
        this.localizedValueConverter = localizedValueConverter;
        this.contactInformationService = contactInformationService;
        this.portfolioRepository = portfolioRepository;
    }

    public ContactInformationDto fetchAndSaveEmployeeContactInformation(long portfolioId, String employeeNumber, Locale locale) {
        ContactInformation contactInformation = fetchEmployeeContactInformation(employeeNumber, locale);
        return contactInformationService.updateContactInformation(portfolioId, new UpdateContactInformation(contactInformation));
    }

    public ContactInformationDto fetchEmployeeContactInformation(long portfolioId, String employeeNumber, Locale locale) {
        ContactInformation contactInformation = fetchEmployeeContactInformation(employeeNumber, locale);
        return contactInformationConverter.toDto(contactInformation, portfolioId);
    }

    private ContactInformation fetchEmployeeContactInformation(String employeeNumber, Locale locale) {
        Optional<ESBEmployeeInfo> esbEmployeeInfoOptional = esbClient.getEmployeeInfo(employeeNumber);
        return esbEmployeeInfoOptional
            .map(esbEmployeeInfo -> getContactInformation(esbEmployeeInfo, locale))
            .orElseGet(ContactInformation::new);
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
