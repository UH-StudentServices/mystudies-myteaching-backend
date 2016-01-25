package fi.helsinki.opintoni.service.converter;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.SpringTest;
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
            new OodiLocalizedValue("fi", "finnish"),
            new OodiLocalizedValue("sv", "swedish"),
            new OodiLocalizedValue("en", "english"));

        assertEquals("finnish", localizedValueConverter.toLocalizedString(oodiLocalizedValues, new Locale("fi")));
    }

    @Test
    public void thatItCanLocalizeToSwedish() {
        List<OodiLocalizedValue> oodiLocalizedValues = Lists.newArrayList(
            new OodiLocalizedValue("fi", "finnish"),
            new OodiLocalizedValue("sv", "swedish"),
            new OodiLocalizedValue("en", "english"));

        assertEquals("swedish", localizedValueConverter.toLocalizedString(oodiLocalizedValues, new Locale("sv")));
    }

    @Test
    public void thatItCanLocalizeToEnglish() {
        List<OodiLocalizedValue> oodiLocalizedValues = Lists.newArrayList(
            new OodiLocalizedValue("fi", "finnish"),
            new OodiLocalizedValue("sv", "swedish"),
            new OodiLocalizedValue("en", "english"));

        assertEquals("english", localizedValueConverter.toLocalizedString(oodiLocalizedValues, Locale.ENGLISH));
    }

    @Test
    public void thatItWillChooseTheDefaultIfMatchingLocaleIsNotFound() {
        List<OodiLocalizedValue> oodiLocalizedValues = Lists.newArrayList(
            new OodiLocalizedValue("fi", "finnish"),
            new OodiLocalizedValue("sv", "swedish"));

        assertEquals("finnish", localizedValueConverter.toLocalizedString(oodiLocalizedValues, Locale.ENGLISH));
    }

    @Test
    public void thatItWillChooseTheFirstIfMatchingOrDefaultLocaleIsNotFound() {
        List<OodiLocalizedValue> oodiLocalizedValues = Lists.newArrayList(
            new OodiLocalizedValue("sv", "swedish"));

        assertEquals("swedish", localizedValueConverter.toLocalizedString(oodiLocalizedValues, Locale.ENGLISH));
    }
}
