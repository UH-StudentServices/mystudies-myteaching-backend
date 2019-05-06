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
import fi.helsinki.opintoni.integration.studyregistry.LocalizedText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
public class LocalizedValueConverter {

    private final String defaultLanguage;

    @Autowired
    public LocalizedValueConverter(AppConfiguration appConfiguration) {
        defaultLanguage = appConfiguration.get("language.default");
    }

    public String toLocalizedString(List<LocalizedText> localizedTexts, Locale locale) {
        return getLocalization(localizedTexts, locale)
            .orElseGet(() -> getDefaultLocalization(localizedTexts)
            .orElseGet(() -> getFirstLocalization(localizedTexts)
            .orElse(null)));
    }

    public String toLocalizedString(Map<String, String> localizedValues, Locale locale) {
        String currentLanguage = locale.getLanguage();

        if (localizedValues == null) {
            return null;
        } else if (localizedValues.containsKey(currentLanguage)) {
            return localizedValues.get(currentLanguage);
        } else if (localizedValues.containsKey(defaultLanguage)) {
            return localizedValues.get(defaultLanguage);
        } else {
            return localizedValues.entrySet().iterator().next().getValue();
        }
    }

    private Optional<String> getLocalization(List<LocalizedText> localizedTexts, Locale locale) {
        return getLocalizationByLanguage(localizedTexts, locale.getLanguage());
    }

    private Optional<String> getDefaultLocalization(List<LocalizedText> localizedTexts) {
        return getLocalizationByLanguage(localizedTexts, defaultLanguage);
    }

    private Optional<String> getFirstLocalization(List<LocalizedText> localizedTexts) {
        return localizedTexts
            .stream()
            .findFirst()
            .map(oodiLocalizedValue -> oodiLocalizedValue.text);
    }

    private Optional<String> getLocalizationByLanguage(List<LocalizedText> localizedTexts, String language) {
        return localizedTexts
            .stream()
            .filter(oodiLocalizedValue -> language.equals(oodiLocalizedValue.langcode.toString()))
            .findFirst()
            .map(oodiLocalizedValue -> oodiLocalizedValue.text);
    }
}
