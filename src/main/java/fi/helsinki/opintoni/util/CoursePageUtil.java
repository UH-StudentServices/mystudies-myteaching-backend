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

import fi.helsinki.opintoni.integration.coursecms.CourseCmsClient;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsCourseUnitRealisation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.studies.StudiesClient;
import fi.helsinki.opintoni.integration.studyregistry.CourseRealisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class CoursePageUtil {

    private static final Logger logger = LoggerFactory.getLogger(CoursePageUtil.class);

    private static final Pattern OLD_COURSE_PAGE_URL_PATTERN =
        Pattern.compile("^https?://(?:dev\\.)?courses\\.helsinki\\.fi/(?:fi|sv|en)/.+/(\\d+)$");

    private final CourseCmsClient courseCmsClient;
    private final CoursePageClient coursePageClient;
    private final StudiesClient studiesClient;

    @Autowired
    public CoursePageUtil(CourseCmsClient courseCmsClient, CoursePageClient coursePageClient, StudiesClient studiesClient) {
        this.courseCmsClient = courseCmsClient;
        this.coursePageClient = coursePageClient;
        this.studiesClient = studiesClient;
    }

    public Map<String, String> getCoursePageUrls(List<? extends CourseRealisation> courses, Locale locale) {
        try {
            return studiesClient.getCoursePageUrls(courses.stream().map(c -> c.realisationId).collect(Collectors.toUnmodifiableList()), locale);
        } catch (Exception e) {
            logger.error("Failed to fetch course page urls", e);
            return Collections.emptyMap();
        }
    }

    public Map<String, CoursePageCourseImplementation> getOldCoursePages(Map<String, String> coursePageUrlsByCourseId, Locale locale) {
        return coursePageUrlsByCourseId.entrySet().stream()
            .map(FunctionHelper.logAndIgnoreExceptions(entry -> getOodiIdFromCoursePageUrl(entry.getValue())
                .map(oodiId -> Map.entry(entry.getKey(), coursePageClient.getCoursePage(oodiId, locale)))
                .orElse(null)))
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, CourseCmsCourseUnitRealisation> getNewCoursePages(Map<String, String> coursePageUrlsByCourseId, Locale locale) {
        return coursePageUrlsByCourseId.entrySet().stream()
            .filter(entry -> getOodiIdFromCoursePageUrl(entry.getValue()).isEmpty())
            .map(FunctionHelper.logAndIgnoreExceptions(entry -> getCourseCmsPage(entry.getKey(), locale)))
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Entry<String, CourseCmsCourseUnitRealisation> getCourseCmsPage(String realisationId, Locale locale) {
        return Map.entry(realisationId, courseCmsClient.getCoursePage(realisationId, locale));
    }

    private Optional<String> getOodiIdFromCoursePageUrl(String coursePageUrl) {
        if (coursePageUrl != null) {
            final Matcher m = OLD_COURSE_PAGE_URL_PATTERN.matcher(coursePageUrl);
            if (m.find()) {
                return Optional.of(m.group(1));
            }
        }
        return Optional.empty();
    }
}
