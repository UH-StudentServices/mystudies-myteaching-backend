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
import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageEvent;
import fi.helsinki.opintoni.integration.oodi.OodiEvent;
import fi.helsinki.opintoni.resolver.EventTypeResolver;
import fi.helsinki.opintoni.resolver.LocationResolver;
import fi.helsinki.opintoni.util.CoursePageUriBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EventConverter {

    private final CoursePageClient coursePageClient;
    private final EventTypeResolver eventTypeResolver;
    private final LocationResolver locationResolver;
    private final CoursePageUriBuilder coursePageUriBuilder;
    private final LocalizedValueConverter localizedValueConverter;

    @Autowired
    public EventConverter(CoursePageClient coursePageClient,
                          EventTypeResolver eventTypeResolver,
                          LocationResolver locationResolver,
                          CoursePageUriBuilder coursePageUriBuilder,
                          LocalizedValueConverter localizedValueConverter) {
        this.coursePageClient = coursePageClient;
        this.eventTypeResolver = eventTypeResolver;
        this.locationResolver = locationResolver;
        this.coursePageUriBuilder = coursePageUriBuilder;
        this.localizedValueConverter = localizedValueConverter;
    }

    public EventDto toDto(CoursePageEvent event) {
        CoursePageCourseImplementation coursePage = coursePageClient.getCoursePage(String.valueOf(event
            .courseImplementationId));
        return new EventDto(
            eventTypeResolver.getEventTypeByCoursePageEvent(event),
            EventDto.Source.COURSE_PAGE,
            event.begin,
            event.end,
            event.courseImplementationId,
            event.where,
            event.title,
            coursePage.title,
            coursePageUriBuilder.getLocalizedUri(coursePage),
            coursePageUriBuilder.getImageUri(coursePage),
            coursePageUriBuilder.getMaterialUri(coursePage),
            coursePage.moodleUrl,
            coursePage.hasMaterial);
    }

    public EventDto toDto(OodiEvent event, Locale locale) {
        CoursePageCourseImplementation coursePage = coursePageClient.getCoursePage(String.valueOf(event.realisationId));
        return new EventDto(
            eventTypeResolver.getEventTypeByOodiTypeCode(event.typeCode),
            EventDto.Source.OODI,
            event.startDate,
            event.endDate,
            event.realisationId,
            getLocations(event),
            localizedValueConverter.toLocalizedString(event.learningOpportunityName, locale),
            coursePage.title,
            coursePageUriBuilder.getLocalizedUri(coursePage),
            coursePageUriBuilder.getImageUri(coursePage),
            coursePageUriBuilder.getMaterialUri(coursePage),
            coursePage.moodleUrl,
            coursePage.hasMaterial,
            locationResolver.getBuilding(event));
    }

    private String getLocations(OodiEvent event) {
        return Lists.newArrayList(event.roomName, event.buildingStreet).stream()
            .filter(Objects::nonNull)
            .collect(Collectors.joining(", "));
    }

}
