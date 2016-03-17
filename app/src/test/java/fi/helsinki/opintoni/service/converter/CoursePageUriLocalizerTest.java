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
