package fi.helsinki.opintoni.service.converter;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.oodi.OodiLocale;
import fi.helsinki.opintoni.integration.oodi.OodiLocalizedValue;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class LocalizedValueConverterTest extends SpringTest {

    @Autowired
    LocalizedValueConverter localizedValueConverter;

    @Test
    public void thatItCanLocalizeToFinnish() {
        List<OodiLocalizedValue> oodiLocalizedValues = Lists.newArrayList(
            new OodiLocalizedValue(OodiLocale.FI, "finnish"),
            new OodiLocalizedValue(OodiLocale.SV, "swedish"),
            new OodiLocalizedValue(OodiLocale.EN, "english"));

        assertEquals("finnish", localizedValueConverter.toLocalizedString(oodiLocalizedValues, new Locale("fi")));
    }

    @Test
    public void thatItCanLocalizeToSwedish() {
        List<OodiLocalizedValue> oodiLocalizedValues = Lists.newArrayList(
            new OodiLocalizedValue(OodiLocale.FI, "finnish"),
            new OodiLocalizedValue(OodiLocale.SV, "swedish"),
            new OodiLocalizedValue(OodiLocale.EN, "english"));

        assertEquals("swedish", localizedValueConverter.toLocalizedString(oodiLocalizedValues, new Locale("sv")));
    }

    @Test
    public void thatItCanLocalizeToEnglish() {
        List<OodiLocalizedValue> oodiLocalizedValues = Lists.newArrayList(
            new OodiLocalizedValue(OodiLocale.FI, "finnish"),
            new OodiLocalizedValue(OodiLocale.SV, "swedish"),
            new OodiLocalizedValue(OodiLocale.EN, "english"));

        assertEquals("english", localizedValueConverter.toLocalizedString(oodiLocalizedValues, Locale.ENGLISH));
    }

    @Test
    public void thatItWillChooseTheDefaultIfMatchingLocaleIsNotFound() {
        List<OodiLocalizedValue> oodiLocalizedValues = Lists.newArrayList(
            new OodiLocalizedValue(OodiLocale.FI, "finnish"),
            new OodiLocalizedValue(OodiLocale.SV, "swedish"));

        assertEquals("finnish", localizedValueConverter.toLocalizedString(oodiLocalizedValues, Locale.ENGLISH));
    }

    @Test
    public void thatItWillChooseTheFirstIfMatchingOrDefaultLocaleIsNotFound() {
        List<OodiLocalizedValue> oodiLocalizedValues = Lists.newArrayList(
            new OodiLocalizedValue(OodiLocale.SV, "swedish"));

        assertEquals("swedish", localizedValueConverter.toLocalizedString(oodiLocalizedValues, Locale.ENGLISH));
    }
}
