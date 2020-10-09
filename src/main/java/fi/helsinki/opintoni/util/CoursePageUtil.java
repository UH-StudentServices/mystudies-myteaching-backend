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
import fi.helsinki.opintoni.integration.IntegrationUtil;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsClient;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsCourseUnitRealisation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.sotka.SotkaClient;
import fi.helsinki.opintoni.integration.studyregistry.CourseRealisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CoursePageUtil {

    private static final List<String> OPEN_UNIVERSITY_ORG_CODES = List.of(
        "H930", // oodi
        "hy-org-48645785" // sisu
    );

    private final AppConfiguration appConfiguration;
    private final LocalDate useNewCoursePageCutOffDate;
    private final SotkaClient sotkaClient;
    private final CourseCmsClient courseCmsClient;
    private final CoursePageClient coursePageClient;

    @Autowired
    public CoursePageUtil(AppConfiguration appConfiguration, CourseCmsClient courseCmsClient, CoursePageClient coursePageClient,
        SotkaClient sotkaClient) {
        this.appConfiguration = appConfiguration;
        this.courseCmsClient = courseCmsClient;
        this.coursePageClient = coursePageClient;
        this.sotkaClient = sotkaClient;

        String useAfterDate = this.appConfiguration.get("courseCms.useAfterDate");
        useNewCoursePageCutOffDate = LocalDate.parse(useAfterDate, DateTimeFormatter.ISO_DATE);
    }

    public boolean useNewCoursePageIntegration(CourseRealisation courseRealisation) {
        if (appConfiguration.getBoolean("courseCms.enabled")) {
            LocalDate courseStartDate = courseRealisation.startDate.toLocalDate();
            return (courseStartDate.isAfter(useNewCoursePageCutOffDate) || courseStartDate.isEqual(useNewCoursePageCutOffDate))
                && courseRealisation.organisations.stream().noneMatch(org -> OPEN_UNIVERSITY_ORG_CODES.contains(org.code));
        }
        return false;
    }

    public Map<String, CoursePageCourseImplementation> getOldCoursePages(List<String> courseIds, Locale locale) {
        Map<Boolean, List<String>> partitionedIds = courseIds.stream()
            .collect(Collectors.groupingBy(FunctionHelper.logAndIgnoreExceptions(
                id -> {
                    try {
                        Integer.parseInt(id);
                        return true;
                    } catch (NumberFormatException e) {
                        return id.startsWith(IntegrationUtil.SISU_COURSE_UNIT_REALISATION_FROM_OODI_ID_PREFIX);
                    }
                })));

        List<Entry<String, String>> optimeOriginated = sotkaClient.getOptimeHierarchies(
            Optional.ofNullable(partitionedIds.get(false)).orElse(List.of()))
            .stream()
            .map(h -> Map.entry(h.optimeId, h.oodiId)).collect(Collectors.toList());

        List<Entry<String, String>> oodiOriginated = Optional.ofNullable(partitionedIds.get(true)).orElse(List.of()).stream()
            .map(id -> Map.entry(id, IntegrationUtil.stripPossibleSisuOodiCurPrefix(id)))
            .collect(Collectors.toList());

        return Stream.concat(optimeOriginated.stream(), oodiOriginated.stream())
            .map(FunctionHelper.logAndIgnoreExceptions(id -> idToCoursePageImplementation(id.getKey(), id.getValue(), locale)))
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private Entry<String, CourseCmsCourseUnitRealisation> getCourseCmsPage(String realisationId, Locale locale) {
        return Map.entry(realisationId, courseCmsClient.getCoursePage(realisationId, locale));
    }

    public Map<String, CourseCmsCourseUnitRealisation> getNewCoursePages(List<String> courseIds, Locale locale) {
        return courseIds.stream()
            .map(FunctionHelper.logAndIgnoreExceptions(id -> getCourseCmsPage(id, locale)))
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private Entry<String, CoursePageCourseImplementation> idToCoursePageImplementation(String courseId, String oodiId, Locale locale) {
        return Map.entry(courseId, coursePageClient.getCoursePage(oodiId, locale));
    }

}
