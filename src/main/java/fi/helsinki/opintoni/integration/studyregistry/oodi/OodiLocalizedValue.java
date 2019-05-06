package fi.helsinki.opintoni.integration.studyregistry.oodi;

public class OodiLocalizedValue {
    public OodiLocale langcode;
    public String text;

    public OodiLocalizedValue() {
    }

    public OodiLocalizedValue(OodiLocale langcode, String text) {
        this.langcode = langcode;
        this.text = text;
    }
}
