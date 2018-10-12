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

    private final int proficiency;

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
