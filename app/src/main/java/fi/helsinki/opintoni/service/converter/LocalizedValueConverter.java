/*
 * This file is part of MystudiesMyteaching application.
 *
 * MystudiesMyteaching application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MystudiesMyteaching application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MystudiesMyteaching application.  If not, see <http://www.gnu.org/licenses/>.
 */

package fi.helsinki.opintoni.service.converter;

import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.integration.oodi.OodiLocalizedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    public String toLocalizedString(Map<String, String> localizedValues, Locale locale) {
        String currentLocale = locale.toString();

        if(localizedValues == null) {
            return null;
        } else if(localizedValues.containsKey(currentLocale)) {
            return localizedValues.get(currentLocale);
        } else if(localizedValues.containsKey(defaultLocale)) {
            return localizedValues.get(defaultLocale);
        } else {
            return localizedValues.entrySet().iterator().next().getValue();
        }
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
            .filter(oodiLocalizedValue -> localeString.equals(oodiLocalizedValue.langcode.toString()))
            .findFirst()
            .map(oodiLocalizedValue -> oodiLocalizedValue.text);
    }
}
