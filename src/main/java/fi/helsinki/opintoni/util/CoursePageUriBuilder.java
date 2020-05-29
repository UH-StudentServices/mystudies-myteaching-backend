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

package fi.helsinki.opintoni.util;

import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsCourseUnitRealisation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class CoursePageUriBuilder {

    private static final Map<String, String> NEW_COURSE_PAGE_LOCALIZED_URL_SLUG = Map.of(
        "fi", "opintotarjonta",
        "sv", "studieutbud",
        "en", "studies"
    );

    private final AppConfiguration appConfiguration;

    @Autowired
    public CoursePageUriBuilder(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public String getMaterialUri(CoursePageCourseImplementation coursePage) {
        return coursePage.url != null ? coursePage.url + "?group-imp-material" : null;
    }

    public String getImageUri(CoursePageCourseImplementation coursePage) {
        return (coursePage != null && coursePage.imageUrl != null)
            ? coursePage.imageUrl
            : appConfiguration.get("coursePage.defaultCourseImageUri");
    }

    public String getImageUri(CourseCmsCourseUnitRealisation coursePage) {
        return coursePage != null && coursePage.courseImage != null && coursePage.courseImage.uri != null
            ? appConfiguration.get("courseCms.base.url") + coursePage.courseImage.uri.url
            : appConfiguration.get("coursePage.defaultCourseImageUri");
    }

    public String getNewCoursePageUri(CourseCmsCourseUnitRealisation coursePage, Locale locale) {
        return coursePage != null && StringUtils.isNotBlank(coursePage.courseUnitRealisationId)
            ? appConfiguration.get("studies.base.url") + "/" +
                NEW_COURSE_PAGE_LOCALIZED_URL_SLUG.get(locale != null ? locale.getLanguage() : "fi") +
                "/cur/" + coursePage.courseUnitRealisationId
            : null;
    }
}
