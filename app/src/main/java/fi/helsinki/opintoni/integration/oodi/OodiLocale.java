package fi.helsinki.opintoni.integration.oodi;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OodiLocale {
    @JsonProperty("fi")
    FI("fi"),

    @JsonProperty("sv")
    SV("sv"),

    @JsonProperty("en")
    EN("en");

    private final String localeString;

    OodiLocale(String localeString) {
        this.localeString = localeString;
    }

    @Override
    public String toString() {
        return localeString;
    }


}
