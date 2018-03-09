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

import fi.helsinki.opintoni.integration.oodi.OodiLocalizedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class EnrollmentNameConverter {

    private static final int REALISATION_ROOT_NAME_LENGTH = 8;
    private static final String REALISATION_NAME_DELIMITER = "... ";

    private final LocalizedValueConverter localizedValueConverter;

    @Autowired
    public EnrollmentNameConverter(LocalizedValueConverter localizedValueConverter) {
        this.localizedValueConverter = localizedValueConverter;
    }

    public String getRealisationNameWithRootName(List<OodiLocalizedValue> realisationName,
        List<OodiLocalizedValue> realisationRootName, Locale locale) {
        String localizedRealisationName = localizedValueConverter.toLocalizedString(realisationName, locale);
        String localizedRealisationRootName = localizedValueConverter.toLocalizedString(realisationRootName, locale);

        if (localizedRealisationRootName != null && !localizedRealisationRootName.equals(localizedRealisationName)) {
            int rootNameLength = Math.min(REALISATION_ROOT_NAME_LENGTH, localizedRealisationRootName.length());
            return String.join(
                REALISATION_NAME_DELIMITER,
                localizedRealisationRootName.substring(0, rootNameLength),
                localizedRealisationName);
        } else {
            return localizedRealisationName;
        }
    }
}
