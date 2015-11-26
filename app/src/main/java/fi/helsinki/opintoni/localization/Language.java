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

package fi.helsinki.opintoni.localization;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Language {

    FI("fi"), SV("sv"), EN("en");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static List<String> getCodes() {
        return Arrays.asList(values()).stream()
            .map(Language::getCode)
            .collect(Collectors.toList());
    }

    public static Language fromCode(String code) {
        return Language.valueOf(code.toUpperCase());
    }
}
