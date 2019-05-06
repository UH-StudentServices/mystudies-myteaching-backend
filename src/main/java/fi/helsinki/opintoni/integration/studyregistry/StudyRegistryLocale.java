package fi.helsinki.opintoni.integration.studyregistry;

public enum StudyRegistryLocale {
    FI("fi"),

    SV("sv"),

    EN("en");

    private final String localeString;

    StudyRegistryLocale(String localeString) {
        this.localeString = localeString;
    }

    @Override
    public String toString() {
        return localeString;
    }
}
