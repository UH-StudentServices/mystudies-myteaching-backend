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

import fi.helsinki.opintoni.dto.CourseRecommendationDto;
import fi.helsinki.opintoni.integration.leiki.LeikiCourseRecommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public class CourseRecommendationConverter {

    private static final String LEIKI_LANG_TAG_PREFIX = "lang_";

    private final CoursePageUriLocalizer coursePageUriLocalizer;

    @Autowired
    public CourseRecommendationConverter(CoursePageUriLocalizer coursePageUriLocalizer) {
        this.coursePageUriLocalizer = coursePageUriLocalizer;
    }

    public CourseRecommendationDto toDto(LeikiCourseRecommendation leikiCourseRecommendation, Locale locale) {
        CourseRecommendationDto courseRecommendationDto = new CourseRecommendationDto();
        courseRecommendationDto.link = getRecommendedCourseLink(leikiCourseRecommendation);
        courseRecommendationDto.title = getRecommendedCourseTitle(leikiCourseRecommendation, locale);
        return courseRecommendationDto;
    }

    private String getRecommendedCourseTitle(LeikiCourseRecommendation leikiCourseRecommendation, Locale locale) {
        return leikiCourseRecommendation.tags.stream()
            .filter(tag -> tag.name.equals(LEIKI_LANG_TAG_PREFIX + locale.getLanguage()))
            .findFirst()
            .flatMap(tag -> tag.values.stream().findFirst())
            .orElse(leikiCourseRecommendation.title);
    }

    private String getRecommendedCourseLink(LeikiCourseRecommendation leikiCourseRecommendation) {
        return Optional
            .ofNullable(leikiCourseRecommendation.link)
            .map(coursePageUriLocalizer::localize)
            .orElse(null);
    }
}
