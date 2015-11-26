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

import com.google.common.collect.ImmutableMap;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.Function;

@Component
public class CoursePageUriLocalizer {

    private final ImmutableMap<String, Function<String, String>> localizers = ImmutableMap.of(
        "fi", this::toFinnish,
        "sv", this::toSwedish,
        "en", this::toEnglish
    );

    public String localize(String originalUrl) {
        Locale locale = LocaleContextHolder.getLocale();
        return localizers.get(locale.getLanguage()).apply(originalUrl);
    }

    private String toFinnish(String coursePageUrl) {
        return coursePageUrl.replaceAll("helsinki.fi/", "helsinki.fi/fi/");
    }

    private String toSwedish(String coursePageUrl) {
        return coursePageUrl.replaceAll("helsinki.fi/", "helsinki.fi/sv/");
    }

    private String toEnglish(String coursePageUrl) {
        return coursePageUrl;
    }
}
