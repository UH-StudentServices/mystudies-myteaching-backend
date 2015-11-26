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

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class LeikiTTypeTest {

    @Test
    public void shouldReturnLeikiTTypeByLocale() {
        LeikiTType sv = LeikiTType.getByLocale(Locale.forLanguageTag("sv"));
        assertEquals(LeikiTType.SV, sv);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnInvalidLocale() {
        LeikiTType sv = LeikiTType.getByLocale(Locale.forLanguageTag("de"));
        assertEquals(LeikiTType.SV, sv);
    }
}
