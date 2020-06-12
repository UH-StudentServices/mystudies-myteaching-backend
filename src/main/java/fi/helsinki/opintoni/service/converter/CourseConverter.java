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
import fi.helsinki.opintoni.integration.coursecms.CourseCmsClient;
import fi.helsinki.opintoni.integration.coursecms.CourseCmsCourseUnitRealisation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.sotka.SotkaClient;
import fi.helsinki.opintoni.integration.studyregistry.CourseRealisation;
import fi.helsinki.opintoni.integration.studyregistry.Enrollment;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryService;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.oodi.courseunitrealisation.Position;
import fi.helsinki.opintoni.resolver.EventTypeResolver;
import fi.helsinki.opintoni.util.CourseMaterialDtoFactory;
import fi.helsinki.opintoni.util.CoursePageUriBuilder;
import fi.helsinki.opintoni.util.CoursePageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CourseConverter {

    private final CoursePageClient coursePageClient;
    private final CourseCmsClient courseCmsClient;
    private final SotkaClient sotkaClient;
    private final StudyRegistryService studyRegistryService;
    private final CoursePageUriBuilder coursePageUriBuilder;
    private final EventTypeResolver eventTypeResolver;
    private final LocalizedValueConverter localizedValueConverter;
    private final CourseMaterialDtoFactory courseMaterialDtoFactory;
    private final EnrollmentNameConverter enrollmentNameConverter;
    private final CoursePageUtil coursePageUtil;

    @Autowired
    public CourseConverter(CoursePageClient coursePageClient,
                           CourseCmsClient courseCmsClient,
                           SotkaClient sotkaClient,
                           StudyRegistryService studyRegistryService,
                           CoursePageUriBuilder coursePageUriBuilder,
                           EventTypeResolver eventTypeResolver,
                           LocalizedValueConverter localizedValueConverter,
                           CourseMaterialDtoFactory courseMaterialDtoFactory,
                           EnrollmentNameConverter enrollmentNameConverter,
                           CoursePageUtil coursePageUtil) {
        this.coursePageClient = coursePageClient;
        this.courseCmsClient = courseCmsClient;
        this.sotkaClient = sotkaClient;
        this.studyRegistryService = studyRegistryService;
        this.coursePageUriBuilder = coursePageUriBuilder;
        this.eventTypeResolver = eventTypeResolver;
        this.localizedValueConverter = localizedValueConverter;
        this.courseMaterialDtoFactory = courseMaterialDtoFactory;
        this.enrollmentNameConverter = enrollmentNameConverter;
        this.coursePageUtil = coursePageUtil;
    }

    public Optional<CourseDto> toDto(Enrollment enrollment, Locale locale) {
        CourseDto dto = null;

        if (!isPositionStudyGroupSet(enrollment.position)) {
            dto = new CourseDto(
                enrollment.learningOpportunityId,
                enrollment.typeCode,
                localizedValueConverter.toLocalizedString(enrollment.name, locale),
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

            enrichWithCoursePageData(dto, enrollment, locale);
        }
        return Optional.ofNullable(dto);
    }

    public Optional<CourseDto> toDto(TeacherCourse teacherCourse, Locale locale, boolean isChildCourseWithoutRoot) {
        CourseDto dto = null;

        if (!isPositionStudyGroupSet(teacherCourse.position)) {
            dto = new CourseDto(
                teacherCourse.learningOpportunityId,
                teacherCourse.realisationTypeCode,
                isChildCourseWithoutRoot ?
                    enrollmentNameConverter.getRealisationNameWithRootName(
                        teacherCourse.realisationName,
                        teacherCourse.realisationRootName,
                        locale) :
                    localizedValueConverter.toLocalizedString(teacherCourse.realisationName, locale),
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

            enrichWithCoursePageData(dto, teacherCourse, locale);
        }
        return Optional.ofNullable(dto);
    }

    private boolean isPositionRoot(String position) {
        return Position.getByValue(position).equals(Position.ROOT);
    }

    private boolean isPositionStudyGroupSet(String position) {
        return Position.getByValue(position).equals(Position.STUDY_GROUP_SET);
    }

    private void enrichWithCoursePageData(CourseDto dto, CourseRealisation courseRealisation, Locale locale) {
        if (coursePageUtil.useNewCoursePageIntegration(courseRealisation)) {
            String realisationId = isPositionRoot(courseRealisation.position) ? dto.realisationId : dto.rootId;
            String optimeId = sotkaClient.getOodiHierarchy(realisationId).optimeId;

            enrichWithCoursePageData(dto, courseCmsClient.getCoursePage(optimeId != null ? optimeId : dto.realisationId, locale), locale);
        } else {
            enrichWithCoursePageData(dto, coursePageClient.getCoursePage(dto.realisationId, locale));
        }
    }

    private void enrichWithCoursePageData(CourseDto dto, CoursePageCourseImplementation coursePage) {
        dto.imageUri = coursePageUriBuilder.getImageUri(coursePage);
        dto.coursePageUri = coursePage.url;
        dto.courseMaterial = courseMaterialDtoFactory.fromCoursePage(coursePage);
    }

    private void enrichWithCoursePageData(CourseDto dto, CourseCmsCourseUnitRealisation coursePage, Locale locale) {
        dto.imageUri = coursePageUriBuilder.getImageUri(coursePage);
        dto.coursePageUri = coursePageUriBuilder.getNewCoursePageUri(coursePage, locale);
        dto.courseMaterial = courseMaterialDtoFactory.fromCoursePage(coursePage);
        // Courses using new course page always have published course page, even without cms data.
        dto.isHidden = false;
    }
}
