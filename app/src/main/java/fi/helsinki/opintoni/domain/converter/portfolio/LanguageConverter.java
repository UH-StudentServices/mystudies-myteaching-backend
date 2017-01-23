package fi.helsinki.opintoni.domain.converter.portfolio;

import fi.helsinki.opintoni.localization.Language;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LanguageConverter implements AttributeConverter<Language, String> {
    @Override
    public String convertToDatabaseColumn(Language lang) {
        return lang.getCode();
    }

    @Override
    public Language convertToEntityAttribute(String langCode) {
        return Language.fromCode(langCode);
    }
}
