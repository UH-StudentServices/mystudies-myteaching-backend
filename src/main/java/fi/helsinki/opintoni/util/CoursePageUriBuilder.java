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
import fi.helsinki.opintoni.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class CoursePageUriBuilder {

    private final AppConfiguration appConfiguration;
    private final SecurityUtils securityUtils;

    @Autowired
    public CoursePageUriBuilder(AppConfiguration appConfiguration, SecurityUtils securityUtils) {
        this.appConfiguration = appConfiguration;
        this.securityUtils = securityUtils;
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
            : appConfiguration.get("courseCms.defaultCourseImageUri");
    }

    public String getCourseUriWithSSO(String courseUri) {
        return securityUtils.getCurrentLogin() != null
            ? UriComponentsBuilder.fromHttpUrl(appConfiguration.get("studies.base.url"))
                .path("/Shibboleth.sso/Login")
                .queryParam("target", URLEncoder.encode(courseUri, UTF_8))
                .build().toUriString()
            : courseUri;
    }
}
