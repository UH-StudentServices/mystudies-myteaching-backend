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

public enum PortfolioLanguage {
    AFRIKAANS("af"),
    ARABIC("ar"),
    CHINESE("zh"),
    CZECH("cs"),
    DANISH("da"),
    DUTCH("nl"),
    ENGLISH("en"),
    ESTONIAN("et"),
    FINNISH("fi"),
    FRENCH("fr"),
    GERMAN("de"),
    GREEK("el"),
    HINDI("hi"),
    HUNGARIAN("hu"),
    ICELANDIC("is"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KOREAN("ko"),
    LATIN("la"),
    LATVIAN("lv"),
    LITHUANIAN("lt"),
    NORWEGIAN("no"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    RUSSIAN("ru"),
    SAMI("se"),
    SLOVAK("sk"),
    SLOVENE("sl"),
    SPANISH("es"),
    SWEDISH("sv"),
    TURKISH("tr");

    private static final Map<String, PortfolioLanguage> PORTFOLIO_LANG_MAP = new HashMap<>();

    static {
        Arrays.asList(PortfolioLanguage.values()).forEach((lang) -> PORTFOLIO_LANG_MAP.put(lang.languageCode, lang));
    }

    private final String languageCode;

    PortfolioLanguage(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    @Override
    @JsonValue
    public String toString() {
        return languageCode;
    }

    @JsonCreator
    public static PortfolioLanguage of(String code) {
        return Optional.ofNullable(PORTFOLIO_LANG_MAP.get(code)).orElseThrow(() ->
            new IllegalArgumentException(String.format("no corresponding language for code '%s'", code)));
    }
}
