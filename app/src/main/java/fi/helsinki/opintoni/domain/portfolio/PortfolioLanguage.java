package fi.helsinki.opintoni.domain.portfolio;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum PortfolioLanguage {
    AFRIKAANS("af"),
    ARABIC("ar"),
    CHINESE("zh"),
    CZECH("cs"),
    DANISH("da"),
    DUTCH("nl"),
    ENGLISH("en"),
    ESTONIAN("et"),
    FINNISH("fi"),
    FRENCH("fr"),
    GERMAN("de"),
    GREEK("el"),
    HINDI("hi"),
    HUNGARIAN("hu"),
    ICELANDIC("is"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KOREAN("ko"),
    LATVIAN("lv"),
    LITHUANIAN("lt"),
    NORWEGIAN("no"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    RUSSIAN("ru"),
    SLOVAK("sk"),
    SLOVENE("sl"),
    SPANISH("es"),
    SWEDISH("sv"),
    TURKISH("tr");

    private static final Map<String, PortfolioLanguage> PORTFOLIO_LANG_MAP = new HashMap<>();

    static {
        Arrays.asList(PortfolioLanguage.values()).forEach((lang) -> {
            PORTFOLIO_LANG_MAP.put(lang.languageCode, lang);
        });
    }

    private String languageCode;

    PortfolioLanguage(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    @Override
    @JsonValue
    public String toString() {
        return languageCode;
    }

    @JsonCreator
    public static PortfolioLanguage of(String code) {
        return Optional.ofNullable(PORTFOLIO_LANG_MAP.get(code)).orElseThrow(() -> {
           return new IllegalArgumentException(String.format("no corresponding language for code '%d'", code));
        });
    }
}
