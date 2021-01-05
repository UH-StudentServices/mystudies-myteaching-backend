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

package fi.helsinki.opintoni.integration.studies;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StudiesMockClient implements StudiesClient {
    private static final Map<String, String> COURSES_BASE_URL_BY_LANGUAGE = Map.of(
        "fi", "opintotarjonta",
        "sv", "studieutbud",
        "en", "courses"
    );

    private final String studiesBaseUrl;

    public StudiesMockClient(String studiesBaseUrl) {
        this.studiesBaseUrl = studiesBaseUrl;
    }

    @Override
    public Map<String, String> getCoursePageUrls(List<String> courseIds, Locale locale) {
        return courseIds.stream()
            .filter(Objects::nonNull)
            .map(courseId -> Map.entry(courseId, getCoursePageUrl(courseId, locale)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String getCoursePageUrl(String courseId, Locale locale) {
        return String.join("/", studiesBaseUrl, COURSES_BASE_URL_BY_LANGUAGE.getOrDefault(locale.getLanguage(), "opintotarjonta"), "cur", courseId);
    }
}
