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

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.studyregistry.oodi.OodiLocale;
import fi.helsinki.opintoni.integration.studyregistry.oodi.LocalizedText;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalizedValueConverterTest extends SpringTest {

    private static final String FINNISH_VALUE = "Finnish value";
    private static final String SWEDISH_VALUE = "Swedish value";
    private static final String ENGLISH_VALUE = "English value";

    @Autowired
    LocalizedValueConverter localizedValueConverter;

    @Test
    public void thatItCanLocalizeToFinnish() {
        List<LocalizedText> oodiLocalizedValues = Lists.newArrayList(
            new LocalizedText(OodiLocale.FI, FINNISH_VALUE),
            new LocalizedText(OodiLocale.SV, SWEDISH_VALUE),
            new LocalizedText(OodiLocale.EN, ENGLISH_VALUE));

        assertThat(FINNISH_VALUE)
            .isEqualTo(localizedValueConverter.toLocalizedString(oodiLocalizedValues, new Locale("fi")));
    }

    @Test
    public void thatItCanLocalizeToSwedish() {
        List<LocalizedText> oodiLocalizedValues = Lists.newArrayList(
            new LocalizedText(OodiLocale.FI, FINNISH_VALUE),
            new LocalizedText(OodiLocale.SV, SWEDISH_VALUE),
            new LocalizedText(OodiLocale.EN, ENGLISH_VALUE));

        assertThat(SWEDISH_VALUE)
            .isEqualTo(localizedValueConverter.toLocalizedString(oodiLocalizedValues, new Locale("sv")));
    }

    @Test
    public void thatItCanLocalizeToEnglish() {
        List<LocalizedText> oodiLocalizedValues = Lists.newArrayList(
            new LocalizedText(OodiLocale.FI, FINNISH_VALUE),
            new LocalizedText(OodiLocale.SV, SWEDISH_VALUE),
            new LocalizedText(OodiLocale.EN, ENGLISH_VALUE));

        assertThat(ENGLISH_VALUE)
            .isEqualTo(localizedValueConverter.toLocalizedString(oodiLocalizedValues, Locale.ENGLISH));
    }

    @Test
    public void thatItWillChooseTheDefaultIfMatchingLocaleIsNotFound() {
        List<LocalizedText> oodiLocalizedValues = Lists.newArrayList(
            new LocalizedText(OodiLocale.FI, FINNISH_VALUE),
            new LocalizedText(OodiLocale.SV, SWEDISH_VALUE));

        assertThat(FINNISH_VALUE)
            .isEqualTo(localizedValueConverter.toLocalizedString(oodiLocalizedValues, Locale.ENGLISH));
    }

    @Test
    public void thatItWillChooseTheFirstIfMatchingOrDefaultLocaleIsNotFound() {
        List<LocalizedText> oodiLocalizedValues = Lists.newArrayList(
            new LocalizedText(OodiLocale.SV, SWEDISH_VALUE));

        assertThat(SWEDISH_VALUE)
            .isEqualTo(localizedValueConverter.toLocalizedString(oodiLocalizedValues, Locale.ENGLISH));
    }
}
