package fi.helsinki.opintoni.service.converter;

import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.integration.oodi.OodiLocalizedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class LocalizedValueConverter {

    private final String defaultLocale;

    @Autowired
    public LocalizedValueConverter(AppConfiguration appConfiguration) {
        defaultLocale = appConfiguration.get("locale.default");
    }

    public String toLocalizedString(List<OodiLocalizedValue> oodiLocalizedValues, Locale locale) {
        return getLocalization(oodiLocalizedValues, locale)
            .orElseGet(() -> getDefaultLocalization(oodiLocalizedValues)
            .orElseGet(() -> getFirstLocalization(oodiLocalizedValues)
            .orElse(null)));
    }

    private Optional<String> getLocalization(List<OodiLocalizedValue> oodiLocalizedValues, Locale locale) {
        return getLocalizationByLocaleString(oodiLocalizedValues, locale.toString());
    }

    private Optional<String> getDefaultLocalization(List<OodiLocalizedValue> oodiLocalizedValues) {
        return getLocalizationByLocaleString(oodiLocalizedValues, defaultLocale);
    }

    private Optional<String> getFirstLocalization(List<OodiLocalizedValue> oodiLocalizedValues) {
        return oodiLocalizedValues
            .stream()
            .findFirst()
            .map(oodiLocalizedValue -> oodiLocalizedValue.text);
    }

    private Optional<String> getLocalizationByLocaleString(List<OodiLocalizedValue> oodiLocalizedValues, String localeString) {
        return oodiLocalizedValues
            .stream()
            .filter(oodiLocalizedValue -> localeString.equals(oodiLocalizedValue.langcode))
            .findFirst()
            .map(oodiLocalizedValue -> oodiLocalizedValue.text);
    }
}
