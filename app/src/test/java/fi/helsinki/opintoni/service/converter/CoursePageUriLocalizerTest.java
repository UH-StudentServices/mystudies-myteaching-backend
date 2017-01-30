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

package fi.helsinki.opintoni.service.converter;

import fi.helsinki.opintoni.SpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.Locale;
import static org.assertj.core.api.Assertions.assertThat;

public class CoursePageUriLocalizerTest extends SpringTest {

    private static final String LOCALE_FI = "fi";
    private static final String LOCALE_EN = "en";
    private static final String LOCALE_SV = "sv";

    @Autowired
    private CoursePageUriLocalizer coursePageUriLocalizer;

    @Test
    public void thatEnglishUriGetsLocalizedToFinnish() {
        setLocale(LOCALE_FI);
        assertThat(coursePageUriLocalizer.localize("http://courses.helsinki.fi/30250")).isEqualTo("http://courses.helsinki.fi/fi/30250");
    }

    @Test
    public void thatEnglishUriGetsLocalizedToSwedish() {
        setLocale(LOCALE_SV);
        assertThat(coursePageUriLocalizer.localize("http://courses.helsinki.fi/30250")).isEqualTo("http://courses.helsinki.fi/sv/30250");
    }

    @Test
    public void thatEnglishUriGetsLocalizedToEnglish() {
        setLocale(LOCALE_EN);
        assertThat(coursePageUriLocalizer.localize("http://courses.helsinki.fi/30250")).isEqualTo("http://courses.helsinki.fi/30250");
    }

    @Test
    public void thatFinnishUriGetsLocalizedToEnglish() {
        setLocale(LOCALE_EN);
        assertThat(coursePageUriLocalizer.localize("http://courses.helsinki.fi/fi/30250")).isEqualTo("http://courses.helsinki.fi/30250");
    }

    @Test
    public void thatFinnishUriGetsLocalizedToSwedish() {
        setLocale(LOCALE_SV);
        assertThat(coursePageUriLocalizer.localize("http://courses.helsinki.fi/fi/30250")).isEqualTo("http://courses.helsinki.fi/sv/30250");
    }

    @Test
    public void thatFinnishUriGetsLocalizedToFinnish() {
        setLocale(LOCALE_FI);
        assertThat(coursePageUriLocalizer.localize("http://courses.helsinki.fi/fi/30250")).isEqualTo("http://courses.helsinki.fi/fi/30250");
    }

    @Test
    public void thatSwedishUriGetsLocalizedToEnglish() {
        setLocale(LOCALE_EN);
        assertThat(coursePageUriLocalizer.localize("http://courses.helsinki.fi/sv/30250")).isEqualTo("http://courses.helsinki.fi/30250");
    }

    @Test
    public void thatSwedishUriGetsLocalizedToSwedish() {
        setLocale(LOCALE_SV);
        assertThat(coursePageUriLocalizer.localize("http://courses.helsinki.fi/sv/30250")).isEqualTo("http://courses.helsinki.fi/sv/30250");
    }

    @Test
    public void thatSwedishUriGetsLocalizedToFinnish() {
        setLocale(LOCALE_FI);
        assertThat(coursePageUriLocalizer.localize("http://courses.helsinki.fi/sv/30250")).isEqualTo("http://courses.helsinki.fi/fi/30250");
    }

    private void setLocale(String localeName) {
        LocaleContextHolder.setLocale(new Locale(localeName));
    }
}
