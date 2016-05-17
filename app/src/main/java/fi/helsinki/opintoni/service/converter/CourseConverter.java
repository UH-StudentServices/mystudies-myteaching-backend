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

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.dto.CourseDto;
import fi.helsinki.opintoni.dto.portfolio.CourseMaterialDto;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiEnrollment;
import fi.helsinki.opintoni.integration.oodi.OodiTeacherCourse;
import fi.helsinki.opintoni.integration.oodi.courseunitrealisation.OodiCourseUnitRealisation;
import fi.helsinki.opintoni.resolver.EventTypeResolver;
import fi.helsinki.opintoni.util.CoursePageUriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.stream.Collectors;

import static fi.helsinki.opintoni.dto.portfolio.CourseMaterialDto.CourseMaterialType.*;

@Component
public class CourseConverter {

    private final CoursePageClient coursePageClient;
    private final OodiClient oodiClient;
    private final CoursePageUriBuilder coursePageUriBuilder;
    private final EventTypeResolver eventTypeResolver;
    private final LocalizedValueConverter localizedValueConverter;

    @Autowired
    public CourseConverter(CoursePageClient coursePageClient,
                           OodiClient oodiClient,
                           CoursePageUriBuilder coursePageUriBuilder,
                           EventTypeResolver eventTypeResolver,
                           LocalizedValueConverter localizedValueConverter) {
        this.coursePageClient = coursePageClient;
        this.oodiClient = oodiClient;
        this.coursePageUriBuilder = coursePageUriBuilder;
        this.eventTypeResolver = eventTypeResolver;
        this.localizedValueConverter = localizedValueConverter;
    }

    public CourseDto toDto(OodiEnrollment oodiEnrollment, Locale locale) {
        CoursePageCourseImplementation coursePage = coursePageClient.getCoursePage(oodiEnrollment.realisationId);
        OodiCourseUnitRealisation courseUnitRealisation =
            oodiClient.getCourseUnitRealisation(oodiEnrollment.realisationId);

        return new CourseDto(
            oodiEnrollment.learningOpportunityId,
            oodiEnrollment.typeCode,
            localizedValueConverter.toLocalizedString(oodiEnrollment.name, locale),
            coursePageUriBuilder.getImageUri(coursePage),
            coursePageUriBuilder.getLocalizedUri(coursePage),
            getCourseMaterial(coursePage),
            oodiEnrollment.webOodiUri,
            oodiEnrollment.startDate,
            oodiEnrollment.endDate,
            oodiEnrollment.realisationId,
            oodiEnrollment.parentId,
            oodiEnrollment.credits,
            courseUnitRealisation.teachers.stream().map(t -> t.fullName).collect(Collectors.toList()),
            eventTypeResolver.isExam(oodiEnrollment.typeCode),
            courseUnitRealisation.isCancelled);
    }

    public CourseDto toDto(OodiTeacherCourse oodiTeacherCourse, Locale locale) {
        CoursePageCourseImplementation coursePage = coursePageClient.getCoursePage(oodiTeacherCourse.realisationId);

        OodiCourseUnitRealisation courseUnitRealisation =
            oodiClient.getCourseUnitRealisation(oodiTeacherCourse.realisationId);

        return new CourseDto(
            oodiTeacherCourse.basecode,
            oodiTeacherCourse.realisationTypeCode,
            localizedValueConverter.toLocalizedString(oodiTeacherCourse.realisationName, locale),
            coursePageUriBuilder.getImageUri(coursePage),
            coursePageUriBuilder.getLocalizedUri(coursePage),
            getCourseMaterial(coursePage),
            oodiTeacherCourse.webOodiUri,
            oodiTeacherCourse.startDate,
            oodiTeacherCourse.endDate,
            oodiTeacherCourse.realisationId,
            oodiTeacherCourse.parentId,
            null,
            Lists.newArrayList(),
            eventTypeResolver.isExam(oodiTeacherCourse.realisationTypeCode),
            courseUnitRealisation.isCancelled);
    }

    private CourseMaterialDto getCourseMaterial(CoursePageCourseImplementation coursePage) {
        if(coursePage == null) {
            return null;
        } else if(coursePage.moodleUrl != null) {
            return new CourseMaterialDto(coursePage.moodleUrl, MOODLE);
        } else if(coursePage.wikiUrl != null) {
            return new CourseMaterialDto(coursePage.wikiUrl, WIKI);
        } else if(coursePage.hasMaterial && coursePage.url != null) {
            return new CourseMaterialDto(coursePageUriBuilder.getMaterialUri(coursePage), COURSE_PAGE);
        } else {
            return null;
        }
    }

}
