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

import com.google.common.collect.ImmutableMap;
import fi.helsinki.opintoni.dto.TeachingLanguageDto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum TeachingLanguages {
    AR(Code.AR, ImmutableMap.of(
        Language.FI.getCode(), "arabia",
        Language.SV.getCode(), "arabiska",
        Language.EN.getCode(), "Arabic")),
    EN(Code.EN, ImmutableMap.of(
        Language.FI.getCode(), "englanti",
        Language.SV.getCode(), "engelska",
        Language.EN.getCode(), "English")),
    ES(Code.ES, ImmutableMap.of(
        Language.FI.getCode(), "espanja",
        Language.SV.getCode(), "spanska",
        Language.EN.getCode(), "Spanish")),
    IT(Code.IT, ImmutableMap.of(
        Language.FI.getCode(), "italia",
        Language.SV.getCode(), "italienska",
        Language.EN.getCode(), "Italian")),
    JA(Code.JA, ImmutableMap.of(
        Language.FI.getCode(), "japani",
        Language.SV.getCode(), "japanska",
        Language.EN.getCode(), "Japanese")),
    ZG(Code.ZG, ImmutableMap.of(
        Language.FI.getCode(), "kiina",
        Language.SV.getCode(), "kinesiska",
        Language.EN.getCode(), "Chinese")),
    KO(Code.KO, ImmutableMap.of(
        Language.FI.getCode(), "korea",
        Language.SV.getCode(), "koreanska",
        Language.EN.getCode(), "Korean")),
    PT(Code.PT, ImmutableMap.of(
        Language.FI.getCode(), "portugali",
        Language.SV.getCode(), "portugisiska",
        Language.EN.getCode(), "Portuguese")),
    FR(Code.FR, ImmutableMap.of(
        Language.FI.getCode(), "ranska",
        Language.SV.getCode(), "franska",
        Language.EN.getCode(), "French")),
    DE(Code.DE, ImmutableMap.of(
        Language.FI.getCode(), "saksa",
        Language.SV.getCode(), "tyska",
        Language.EN.getCode(), "German")),
    DA(Code.DA, ImmutableMap.of(
        Language.FI.getCode(), "tanska",
        Language.SV.getCode(), "danska",
        Language.EN.getCode(), "Danish")),
    RU(Code.RU, ImmutableMap.of(
        Language.FI.getCode(), "venäjä",
        Language.SV.getCode(), "ryska",
        Language.EN.getCode(), "Russian")),
    ET(Code.ET, ImmutableMap.of(
        Language.FI.getCode(), "viro",
        Language.SV.getCode(), "estniska",
        Language.EN.getCode(), "Estonian")),
    FI_1ST_SC(Code.FI_1ST_SC, ImmutableMap.of(
        Language.FI.getCode(), "äidinkieli (suomi), puheviestintä- ja vuorovaikutustaidot",
        Language.SV.getCode(), "modersmål (finska), retorik",
        Language.EN.getCode(), "Finnish as a native language, speech communication")),
    FI_1ST_AW(Code.FI_1ST_AW, ImmutableMap.of(
        Language.FI.getCode(), "äidinkieli (suomi), akateemiset tekstitaidot",
        Language.SV.getCode(), "modersmål (finska), akademiskt skrivande",
        Language.EN.getCode(), "Finnish as a native language, academic writing")),
    FI_2ND(Code.FI_2ND, ImmutableMap.of(
        Language.FI.getCode(), "suomi toisena kotimaisena kielenä (finska)",
        Language.SV.getCode(), "finska som andra inhemska språk",
        Language.EN.getCode(), "Finnish for native speakers of Swedish")),
    SV_1ST(Code.SV_1ST, ImmutableMap.of(
        Language.FI.getCode(), "äidinkieli (ruotsi)",
        Language.SV.getCode(), "modersmål (svenska)",
        Language.EN.getCode(), "Swedish as a native language")),
    SV_2ND(Code.SV_2ND, ImmutableMap.of(
        Language.FI.getCode(), "ruotsi toisena kotimaisena kielenä",
        Language.SV.getCode(), "svenska som andra inhemska språk",
        Language.EN.getCode(), "Swedish for native speakers of Finnish")),
    SLU(Code.SLU, ImmutableMap.of(
        Language.FI.getCode(), "pedagoginen yliopistonlehtori",
        Language.SV.getCode(), "pedagogisk universitetslektor",
        Language.EN.getCode(), "senior lecturer in university pedagogy"));

    public enum Code {
        AR("ar"), EN("en"), ES("es"), IT("it"), JA("ja"), ZG("zg"), KO("ko"), PT("pt"), FR("fr"), DE("de"), DA("da"),
        RU("ru"), ET("et"), FI_1ST_SC("fi_1st_sc"), FI_1ST_AW("fi_1st_aw"), FI_2ND("fi_2nd"), SV_1ST("sv_1st"),
        SV_2ND("sv_2nd"), SLU("slu");

        private final String code;

        Code(String code) {
            this.code = code;
        }

        public static Code fromString(String code) {
            return Optional.of(Code.valueOf(code.toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException(""));
        }

        public String getCode() {
            return this.code;
        }
    }

    private final Code code;
    private final ImmutableMap<String, String> localizedNames;

    TeachingLanguages(Code code, ImmutableMap<String, String> localizedNames) {
        this.code = code;
        this.localizedNames = localizedNames;
    }

    public static List<String> getCodes() {
        return Arrays.stream(TeachingLanguages.values()).map(TeachingLanguages::getCode).collect(Collectors.toList());
    }

    public static TeachingLanguages fromCode(String code) {
        return Optional.of(TeachingLanguages.valueOf(code.toUpperCase()))
            .orElseThrow(() -> new IllegalArgumentException(String.format("no corresponding teaching language for code %s", code)));
    }

    public TeachingLanguageDto toDto() {
        return new TeachingLanguageDto(code.getCode(), localizedNames);
    }

    public String getCode() {
        return code.getCode();
    }

    public String getNameFor(String langCode) {
        return localizedNames.get(langCode);
    }
}
