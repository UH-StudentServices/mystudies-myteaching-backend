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

import fi.helsinki.opintoni.dto.profile.CourseMaterialDto;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsCourseUnitRealisation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fi.helsinki.opintoni.dto.profile.CourseMaterialDto.CourseMaterialType.COURSE_PAGE;
import static fi.helsinki.opintoni.dto.profile.CourseMaterialDto.CourseMaterialType.MOODLE;
import static fi.helsinki.opintoni.dto.profile.CourseMaterialDto.CourseMaterialType.WIKI;

@Component
public class CourseMaterialDtoFactory {
    private final CoursePageUriBuilder coursePageUriBuilder;

    @Autowired
    public CourseMaterialDtoFactory(CoursePageUriBuilder coursePageUriBuilder) {
        this.coursePageUriBuilder = coursePageUriBuilder;
    }

    public CourseMaterialDto fromCoursePage(CoursePageCourseImplementation coursePage) {
        if (coursePage == null) {
            return null;
        } else if (coursePage.moodleUrl != null) {
            return new CourseMaterialDto(coursePage.moodleUrl, MOODLE);
        } else if (coursePage.wikiUrl != null) {
            return new CourseMaterialDto(coursePage.wikiUrl, WIKI);
        } else if (coursePage.hasMaterial && coursePage.url != null) {
            return new CourseMaterialDto(coursePageUriBuilder.getMaterialUri(coursePage), COURSE_PAGE);
        } else {
            return null;
        }
    }

    public CourseMaterialDto fromCoursePage(CourseCmsCourseUnitRealisation coursePage) {
        if (coursePage != null && coursePage.moodleLink != null && StringUtils.isNotBlank(coursePage.moodleLink.uri)) {
            return new CourseMaterialDto(coursePage.moodleLink.uri, MOODLE);
        }
        return null;
    }
}
