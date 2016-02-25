package fi.helsinki.opintoni.dto.portfolio;

import java.util.List;

public class LanguageProficienciesChangeDescriptorDto {
    public List<Long> deletedIds;
    public List<LanguageProficiencyDto> newLanguageProficiencies;
    public List<LanguageProficiencyDto> updatedLanguageProficiencies;
}
