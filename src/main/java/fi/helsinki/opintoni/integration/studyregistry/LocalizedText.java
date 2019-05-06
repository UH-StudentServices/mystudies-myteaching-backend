package fi.helsinki.opintoni.integration.studyregistry;

public class LocalizedText {
    public StudyRegistryLocale langcode;
    public String text;

    public LocalizedText() {
    }

    public LocalizedText(StudyRegistryLocale langcode, String text) {
        this.langcode = langcode;
        this.text = text;
    }
}
