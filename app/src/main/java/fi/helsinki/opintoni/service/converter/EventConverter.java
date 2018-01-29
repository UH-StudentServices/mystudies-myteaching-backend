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

import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageEvent;
import fi.helsinki.opintoni.integration.oodi.OodiEvent;
import fi.helsinki.opintoni.resolver.EventTypeResolver;
import fi.helsinki.opintoni.resolver.LocationResolver;
import fi.helsinki.opintoni.util.CourseMaterialDtoFactory;
import fi.helsinki.opintoni.util.CoursePageUriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EventConverter {

    private final CoursePageClient coursePageClient;
    private final EventTypeResolver eventTypeResolver;
    private final LocationResolver locationResolver;
    private final CoursePageUriBuilder coursePageUriBuilder;
    private final CourseMaterialDtoFactory courseMaterialDtoFactory;
    private final EnrollmentNameConverter enrollmentNameConverter;

    @Autowired
    public EventConverter(CoursePageClient coursePageClient,
                          EventTypeResolver eventTypeResolver,
                          LocationResolver locationResolver,
                          CoursePageUriBuilder coursePageUriBuilder,
                          CourseMaterialDtoFactory courseMaterialDtoFactory,
                          EnrollmentNameConverter enrollmentNameConverter) {
        this.coursePageClient = coursePageClient;
        this.eventTypeResolver = eventTypeResolver;
        this.locationResolver = locationResolver;
        this.coursePageUriBuilder = coursePageUriBuilder;
        this.courseMaterialDtoFactory = courseMaterialDtoFactory;
        this.enrollmentNameConverter = enrollmentNameConverter;
    }

    public EventDto toDto(CoursePageEvent event, CoursePageCourseImplementation coursePage) {
        return new EventDto(
            eventTypeResolver.getEventTypeByCoursePageEvent(event),
            EventDto.Source.COURSE_PAGE,
            event.begin,
            event.end,
            coursePage.courseImplementationId,
            event.title,
            coursePage.title,
            coursePage.url,
            coursePageUriBuilder.getImageUri(coursePage),
            courseMaterialDtoFactory.fromCoursePage(coursePage),
            coursePage.moodleUrl,
            coursePage.hasMaterial,
            locationResolver.getLocation(event));
    }

    public EventDto toDto(OodiEvent event, CoursePageCourseImplementation coursePage, Locale locale) {
        return new EventDto(
            eventTypeResolver.getEventTypeByOodiTypeCode(event.typeCode),
            EventDto.Source.OODI,
            event.startDate,
            event.endDate,
            event.realisationId,
            enrollmentNameConverter.getRealisationNameWithRootName(event.realisationName, event.realisationRootName, locale),
            coursePage.title,
            coursePage.url,
            coursePageUriBuilder.getImageUri(coursePage),
            courseMaterialDtoFactory.fromCoursePage(coursePage),
            coursePage.moodleUrl,
            coursePage.hasMaterial,
            locationResolver.getLocation(event));
    }

}
