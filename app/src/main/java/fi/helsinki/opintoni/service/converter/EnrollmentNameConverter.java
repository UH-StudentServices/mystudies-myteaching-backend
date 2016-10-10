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

    public String getRealisationNameWithRootName(List<OodiLocalizedValue> realisationName, List<OodiLocalizedValue> realisationRootName, Locale locale) {
        String localizedRealisationName = localizedValueConverter.toLocalizedString(realisationName, locale);
        String localizedRealisationRootName = localizedValueConverter.toLocalizedString(realisationRootName, locale);

        if(localizedRealisationRootName != null && !localizedRealisationRootName.equals(localizedRealisationName)) {
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
