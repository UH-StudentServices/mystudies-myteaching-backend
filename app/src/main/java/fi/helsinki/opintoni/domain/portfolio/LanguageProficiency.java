package fi.helsinki.opintoni.domain.portfolio;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum LanguageProficiency {
    ELEMENTARY_PROFICIENCY(1),
    LIMITED_WORKING_PROFICIENCY(2),
    PROFESSIONAL_WORKING_PROFICIENCY(3),
    FULL_PROFESSIONAL_PROFICIENCY(4),
    NATIVE_PROFICIENCY(5);

    private static final Map<Integer, LanguageProficiency> PROFICIENCY_VALUE_MAP = new HashMap<>();

    static {
        Arrays.asList(LanguageProficiency.values()).forEach((languageProficiency) ->
            PROFICIENCY_VALUE_MAP.put(languageProficiency.proficiency, languageProficiency));
    }

    private int proficiency;

    LanguageProficiency(int proficiency) {
        this.proficiency = proficiency;
    }

    @JsonValue
    public int getProficiency() {
        return proficiency;
    }

    @Override
    public String toString() {
        return String.valueOf(proficiency);
    }

    @JsonCreator
    public static LanguageProficiency of(int proficiency) {
        return Optional.ofNullable(PROFICIENCY_VALUE_MAP.get(proficiency)).orElseThrow(() -> new IllegalArgumentException(
            String.format("no corresponding language proficiency for '%d'", proficiency)
        ));
    }
}
