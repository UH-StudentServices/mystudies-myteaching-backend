package fi.helsinki.opintoni.domain.converter.portfolio;

import fi.helsinki.opintoni.domain.portfolio.PortfolioLanguage;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PortfolioLanguageConverter implements AttributeConverter<PortfolioLanguage, String> {
    @Override
    public String convertToDatabaseColumn(PortfolioLanguage lang) {
        return lang.getLanguageCode();
    }

    @Override
    public PortfolioLanguage convertToEntityAttribute(String languageCode) {
        return PortfolioLanguage.of(languageCode);
    }
}
