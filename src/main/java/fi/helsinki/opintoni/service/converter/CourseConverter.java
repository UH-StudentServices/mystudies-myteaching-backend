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
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.studyregistry.Enrollment;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryService;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.oodi.courseunitrealisation.Position;
import fi.helsinki.opintoni.resolver.EventTypeResolver;
import fi.helsinki.opintoni.util.CourseMaterialDtoFactory;
import fi.helsinki.opintoni.util.CoursePageUriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CourseConverter {

    private final CoursePageClient coursePageClient;
    private final StudyRegistryService studyRegistryService;
    private final CoursePageUriBuilder coursePageUriBuilder;
    private final EventTypeResolver eventTypeResolver;
    private final LocalizedValueConverter localizedValueConverter;
    private final CourseMaterialDtoFactory courseMaterialDtoFactory;
    private final EnrollmentNameConverter enrollmentNameConverter;

    @Autowired
    public CourseConverter(CoursePageClient coursePageClient,
                           StudyRegistryService studyRegistryService,
                           CoursePageUriBuilder coursePageUriBuilder,
                           EventTypeResolver eventTypeResolver,
                           LocalizedValueConverter localizedValueConverter,
                           CourseMaterialDtoFactory courseMaterialDtoFactory,
                           EnrollmentNameConverter enrollmentNameConverter) {
        this.coursePageClient = coursePageClient;
        this.studyRegistryService = studyRegistryService;
        this.coursePageUriBuilder = coursePageUriBuilder;
        this.eventTypeResolver = eventTypeResolver;
        this.localizedValueConverter = localizedValueConverter;
        this.courseMaterialDtoFactory = courseMaterialDtoFactory;
        this.enrollmentNameConverter = enrollmentNameConverter;
    }

    public Optional<CourseDto> toDto(Enrollment enrollment, Locale locale) {
        CourseDto dto = null;

        if (!isPositionStudygroupset(enrollment.position)) {
            CoursePageCourseImplementation coursePage = coursePageClient.getCoursePage(enrollment.realisationId, locale);

            dto = new CourseDto(
                enrollment.learningOpportunityId,
                enrollment.typeCode,
                localizedValueConverter.toLocalizedString(enrollment.name, locale),
                coursePageUriBuilder.getImageUri(coursePage),
                coursePage.url,
                courseMaterialDtoFactory.fromCoursePage(coursePage),
                enrollment.startDate,
                enrollment.endDate,
                enrollment.realisationId,
                enrollment.parentId,
                enrollment.rootId,
                enrollment.credits,
                studyRegistryService.getCourseRealisationTeachers(enrollment.realisationId)
                    .stream().map(t -> t.name).collect(Collectors.toList()),
                eventTypeResolver.isExam(enrollment.typeCode),
                enrollment.isCancelled,
                enrollment.isHidden,
                null);

        }
        return Optional.ofNullable(dto);
    }

    public Optional<CourseDto> toDto(TeacherCourse teacherCourse, Locale locale, boolean isChildCourseWithoutRoot) {
        CourseDto dto = null;

        if (!isPositionStudygroupset(teacherCourse.position)) {
            CoursePageCourseImplementation coursePage = coursePageClient.getCoursePage(teacherCourse.realisationId, locale);

            dto = new CourseDto(
                teacherCourse.learningOpportunityId,
                teacherCourse.realisationTypeCode,
                isChildCourseWithoutRoot ?
                    enrollmentNameConverter.getRealisationNameWithRootName(
                        teacherCourse.realisationName,
                        teacherCourse.realisationRootName,
                        locale) :
                    localizedValueConverter.toLocalizedString(teacherCourse.realisationName, locale),
                coursePageUriBuilder.getImageUri(coursePage),
                coursePage.url,
                courseMaterialDtoFactory.fromCoursePage(coursePage),
                teacherCourse.startDate,
                teacherCourse.endDate,
                teacherCourse.realisationId,
                teacherCourse.parentId,
                teacherCourse.rootId,
                null,
                Lists.newArrayList(),
                eventTypeResolver.isExam(teacherCourse.realisationTypeCode),
                teacherCourse.isCancelled,
                teacherCourse.isHidden,
                teacherCourse.teacherRole);
        }
        return Optional.ofNullable(dto);
    }

    private boolean isPositionStudygroupset(String position) {
        return Position.getByValue(position).equals(Position.STUDY_GROUP_SET);
    }
}
