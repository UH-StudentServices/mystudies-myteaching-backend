package fi.helsinki.opintoni.service.converter.portfolio;

import fi.helsinki.opintoni.domain.portfolio.PortfolioLanguageProficiency;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import org.springframework.stereotype.Component;

@Component
public class LanguageProficiencyConverter {
    public LanguageProficiencyDto toDto(PortfolioLanguageProficiency languageProficiency) {
        LanguageProficiencyDto languageProficiencyDto = new LanguageProficiencyDto();
        languageProficiencyDto.id = languageProficiency.id;
        languageProficiencyDto.language = languageProficiency.languageCode;
        languageProficiencyDto.proficiency = languageProficiency.proficiency;
        return languageProficiencyDto;
    }
}
