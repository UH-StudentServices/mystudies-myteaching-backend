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
import fi.helsinki.opintoni.integration.studyregistry.CourseRealisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CoursePageUtil {

    private static final String OPEN_UNIVERSITY_ORG_CODE = "H930";

    private final AppConfiguration appConfiguration;
    private final LocalDate useNewCoursePageCutOffDate;

    @Autowired
    public CoursePageUtil(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
        String useAfterDate = this.appConfiguration.get("courseCms.useAfterDate");
        useNewCoursePageCutOffDate = LocalDate.parse(useAfterDate, DateTimeFormatter.ISO_DATE);
    }

    public boolean useNewCoursePageIntegration(CourseRealisation courseRealisation) {
        if (appConfiguration.getBoolean("courseCms.enabled")) {
            LocalDate courseStartDate = courseRealisation.startDate.toLocalDate();
            return (courseStartDate.isAfter(useNewCoursePageCutOffDate) || courseStartDate.isEqual(useNewCoursePageCutOffDate))
                && courseRealisation.organisations.stream().noneMatch(org -> org.code.equals(OPEN_UNIVERSITY_ORG_CODE));
        }
        return false;
    }
}
