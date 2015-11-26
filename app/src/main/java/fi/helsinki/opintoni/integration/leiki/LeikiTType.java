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

package fi.helsinki.opintoni.integration.leiki;

import java.util.Locale;

public enum LeikiTType {

    FI("kaikki_fi", Locale.forLanguageTag("fi")),
    SV("kaikki_sv", Locale.forLanguageTag("sv")),
    EN("kaikki_en", Locale.forLanguageTag("en"));

    private String value;

    private Locale locale;

    private LeikiTType(String value, Locale locale) {
        this.value = value;
        this.locale = locale;
    }

    public static LeikiTType getByLocale(Locale locale) {
        for (LeikiTType leikiTType : LeikiTType.values()) {
            if(leikiTType.locale.equals(locale)) {
                return leikiTType;
            }
        }
        throw new IllegalArgumentException("No Leiki t_type available with locale " + locale.toLanguageTag());
    }

    public String getValue() {
        return value;
    }
}
