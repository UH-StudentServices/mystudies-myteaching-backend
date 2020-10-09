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

import fi.helsinki.opintoni.dto.CourseDto;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsCourseUnitRealisation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.studyregistry.Enrollment;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.resolver.EventTypeResolver;
import fi.helsinki.opintoni.util.CourseMaterialDtoFactory;
import fi.helsinki.opintoni.util.CoursePageUriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class CourseConverter {

    private final CoursePageUriBuilder coursePageUriBuilder;
    private final EventTypeResolver eventTypeResolver;
    private final LocalizedValueConverter localizedValueConverter;
    private final CourseMaterialDtoFactory courseMaterialDtoFactory;

    @Autowired
    public CourseConverter(CoursePageUriBuilder coursePageUriBuilder, EventTypeResolver eventTypeResolver,
        LocalizedValueConverter localizedValueConverter, CourseMaterialDtoFactory courseMaterialDtoFactory) {
        this.coursePageUriBuilder = coursePageUriBuilder;
        this.eventTypeResolver = eventTypeResolver;
        this.localizedValueConverter = localizedValueConverter;
        this.courseMaterialDtoFactory = courseMaterialDtoFactory;
    }

    // not used, todo cleanup
    public Optional<CourseDto> toDto(Enrollment enrollment, Locale locale) {
        CourseDto dto = null;
        return Optional.ofNullable(dto);
    }

    public CourseDto toDto(TeacherCourse teacherCourse, CoursePageCourseImplementation oldCoursePage, CourseCmsCourseUnitRealisation newCoursePage,
        Locale locale) {
        CourseDto dto = new CourseDto(
            teacherCourse.learningOpportunityId,
            teacherCourse.realisationTypeCode,
            localizedValueConverter.toLocalizedString(teacherCourse.realisationName, locale),
            teacherCourse.startDate,
            teacherCourse.endDate,
            teacherCourse.realisationId,
            teacherCourse.parentId,
            teacherCourse.rootId,
            null,
            List.of(),
            eventTypeResolver.isExam(teacherCourse.realisationTypeCode),
            teacherCourse.isCancelled,
            teacherCourse.isHidden,
            teacherCourse.teacherRole);

        if (oldCoursePage != null) {
            enrichWithOldCoursePageData(dto, oldCoursePage);
        }

        if (newCoursePage != null) {
            enrichWithNewCoursePageData(dto, newCoursePage, locale);
        }

        return dto;
    }

    private void enrichWithOldCoursePageData(CourseDto dto, CoursePageCourseImplementation coursePage) {
        dto.imageUri = coursePageUriBuilder.getImageUri(coursePage);
        dto.coursePageUri = coursePage.url;
        dto.courseMaterial = courseMaterialDtoFactory.fromCoursePage(coursePage);
    }

    private void enrichWithNewCoursePageData(CourseDto dto, CourseCmsCourseUnitRealisation coursePage, Locale locale) {
        dto.imageUri = coursePageUriBuilder.getImageUri(coursePage);
        dto.coursePageUri = coursePageUriBuilder.getNewCoursePageUri(coursePage, locale);
        dto.courseMaterial = courseMaterialDtoFactory.fromCoursePage(coursePage);
        // Courses using new course page always have published course page, even without cms data.
        dto.isHidden = false;
    }

}
