package fi.helsinki.opintoni.integration.oodi;

public class OodiLocalizedValue {
    public String langcode;
    public String text;

    public OodiLocalizedValue() {
    }

    public OodiLocalizedValue(String langcode, String text) {
        this.langcode = langcode;
        this.text = text;
    }
}
