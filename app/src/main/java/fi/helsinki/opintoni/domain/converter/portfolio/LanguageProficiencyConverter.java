package fi.helsinki.opintoni.domain.converter.portfolio;

import fi.helsinki.opintoni.domain.portfolio.LanguageProficiency;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LanguageProficiencyConverter implements AttributeConverter<LanguageProficiency, Integer> {
    @Override
    public Integer convertToDatabaseColumn(LanguageProficiency proficiency) {
        return proficiency.getProficiency();
    }

    @Override
    public LanguageProficiency convertToEntityAttribute(Integer proficiencyCode) {
        return LanguageProficiency.of(proficiencyCode);
    }
}
