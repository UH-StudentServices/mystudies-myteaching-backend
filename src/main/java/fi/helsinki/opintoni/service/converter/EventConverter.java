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

import com.google.common.collect.ImmutableList;
import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.dto.EventDtoBuilder;
import fi.helsinki.opintoni.dto.OptimeExtrasDto;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageEvent;
import fi.helsinki.opintoni.integration.studyregistry.Event;
import fi.helsinki.opintoni.resolver.EventTypeResolver;
import fi.helsinki.opintoni.resolver.LocationResolver;
import fi.helsinki.opintoni.service.TimeService;
import fi.helsinki.opintoni.util.CourseMaterialDtoFactory;
import fi.helsinki.opintoni.util.CoursePageUriBuilder;
import fi.helsinki.opintoni.util.TimeZoneUtils;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static fi.helsinki.opintoni.dto.LocationDto.getLocationsFromString;
import static fi.helsinki.opintoni.service.TimeService.HELSINKI_ZONE_ID;

@Component
public class EventConverter {

    private final EventTypeResolver eventTypeResolver;
    private final LocationResolver locationResolver;
    private final CoursePageUriBuilder coursePageUriBuilder;
    private final CourseMaterialDtoFactory courseMaterialDtoFactory;
    private final EnrollmentNameConverter enrollmentNameConverter;
    private final TimeZoneUtils timeZoneUtils;
    private final TimeService timeService;

    @Autowired
    public EventConverter(EventTypeResolver eventTypeResolver,
                          LocationResolver locationResolver,
                          CoursePageUriBuilder coursePageUriBuilder,
                          CourseMaterialDtoFactory courseMaterialDtoFactory,
                          EnrollmentNameConverter enrollmentNameConverter,
                          TimeZoneUtils timeZoneUtils,
                          TimeService timeService) {
        this.eventTypeResolver = eventTypeResolver;
        this.locationResolver = locationResolver;
        this.coursePageUriBuilder = coursePageUriBuilder;
        this.courseMaterialDtoFactory = courseMaterialDtoFactory;
        this.enrollmentNameConverter = enrollmentNameConverter;
        this.timeZoneUtils = timeZoneUtils;
        this.timeService = timeService;
    }

    public EventDto toDto(CoursePageEvent event, CoursePageCourseImplementation coursePage) {
        return new EventDtoBuilder()
            .setType(eventTypeResolver.getEventTypeByCoursePageEvent(event))
            .setSource(EventDto.Source.COURSE_PAGE)
            .setStartDate(event.begin)
            .setEndDate(event.end)
            .setRealisationId(coursePage.courseImplementationId)
            .setTitle(event.title)
            .setCourseTitle(coursePage.title)
            .setCourseUri(coursePage.url).setCourseImageUri(coursePageUriBuilder.getImageUri(coursePage))
            .setCourseMaterialDto(courseMaterialDtoFactory.fromCoursePage(coursePage))
            .setMoodleUri(coursePage.moodleUrl)
            .setHasMaterial(coursePage.hasMaterial)
            .setLocations(ImmutableList.of(locationResolver.getLocation(event)))
            .createEventDto();
    }

    public EventDto toDto(Event event, CoursePageCourseImplementation coursePage, Locale locale) {
        return new EventDtoBuilder()
            .setType(eventTypeResolver.getEventTypeByOodiTypeCode(event.typeCode))
            .setSource(EventDto.Source.STUDY_REGISTRY)
            .setStartDate(event.startDate)
            .setEndDate(event.endDate)
            .setRealisationId(event.realisationId)
            .setTitle(enrollmentNameConverter.getRealisationNameWithRootName(event.realisationName, event.realisationRootName, locale))
            .setCourseTitle(coursePage.title)
            .setCourseUri(coursePage.url)
            .setCourseImageUri(coursePageUriBuilder.getImageUri(coursePage))
            .setCourseMaterialDto(courseMaterialDtoFactory.fromCoursePage(coursePage))
            .setMoodleUri(coursePage.moodleUrl)
            .setHasMaterial(coursePage.hasMaterial)
            .setLocations(locationResolver.getLocations(event))
            .setOptimeExtras(event.optimeExtras == null ? null :
                new OptimeExtrasDto(event.optimeExtras.otherNotes, event.optimeExtras.staffNotes))
            .setHidden(event.isHidden)
            .createEventDto();
    }

    public EventDto toDto(VEvent v) {
        String eventDescription = v.getDescription() != null ? v.getDescription().getValue() : null;
        return new EventDtoBuilder()
            .setSource(EventDto.Source.OPTIME)
            .setStartDate(toLocalDateTime(v.getStartDate().getDate()))
            .setEndDate(toLocalDateTime(v.getEndDate().getDate()))
            .setTitle(optimeEventTitleFromSummaryAndDescription(v.getSummary().getValue(), eventDescription))
            .setOptimeExtras(eventDescription != null ? OptimeExtrasDto.parse(eventDescription) : null)
            .setLocations(getLocationsFromString(v.getLocation().getValue()))
            .setUid(v.getUid().getValue())
            .createEventDto();
    }

    public List<EventDto> toDtos(InputStream calendarContent) {
        CalendarBuilder builder = new CalendarBuilder();

        try {
            Calendar calendar = builder.build(calendarContent);
            List<VEvent> events = calendar.getComponents(net.fortuna.ical4j.model.Component.VEVENT);

            return events
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toICalendar(List<EventDto> events) {
        VTimeZone timeZone = timeZoneUtils.getHelsinkiTimeZone();

        Calendar calendar = new Calendar();
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getComponents().add(timeZone);

        events.forEach(e -> {
            VEvent event = eventDtoToVEvent(e);
            calendar.getComponents().add(event);
        });

        return calendar.toString();
    }

    private Uid uid(String uidOrNull) {
        return uidOrNull == null ? new Uid(UUID.randomUUID().toString()) : new Uid(uidOrNull);
    }

    private VEvent eventDtoToVEvent(EventDto eventDto) {
        PropertyList eventProperties = new PropertyList();
        eventProperties.add(new DtStart(convertStartDateToCalDate(eventDto)));
        eventProperties.add(new DtEnd(convertEndDateToCalDate(eventDto)));
        eventProperties.add(new Summary(eventDto.getFullEventTitle()));
        if (!eventDto.getOptimeExtrasAsString().isEmpty()) {
            eventProperties.add(new Description(eventDto.getOptimeExtrasAsString()));
        }
        eventProperties.add(new Location(eventDto.getLocationsAsString()));
        eventProperties.add(uid(eventDto.uid));

        return new VEvent(eventProperties);
    }

    private DateTime convertStartDateToCalDate(EventDto eventDto) {
        return calDateTimeFromLocalDateTime(eventDto.startDate);
    }

    private DateTime calDateTimeFromLocalDateTime(LocalDateTime localDateTime) {
        DateTime dateTime = new DateTime(Date.from(localDateTime.atZone(HELSINKI_ZONE_ID).toInstant()));

        dateTime.setTimeZone(new net.fortuna.ical4j.model.TimeZone(timeZoneUtils.getHelsinkiTimeZone()));

        return dateTime;
    }

    private DateTime convertEndDateToCalDate(EventDto eventDto) {
        if (eventDto.endDate == null) {
            LocalDateTime endOfDay = timeService.endOfDay(eventDto.startDate);
            return calDateTimeFromLocalDateTime(endOfDay);
        }

        return calDateTimeFromLocalDateTime(eventDto.endDate);
    }

    private LocalDateTime toLocalDateTime(Date dt) {
        return LocalDateTime.ofInstant(dt.toInstant(), HELSINKI_ZONE_ID);
    }

    private String optimeEventTitleFromSummaryAndDescription(String summary, String description) {
        String descriptionTitle = parseOptimeEventTitleFromDescription(description);
        return descriptionTitle.isBlank() ? summary : String.format("%s, %s", summary, descriptionTitle);
    }

    private String parseOptimeEventTitleFromDescription(String description) {
        if (StringUtils.isBlank(description)) {
            return "";
        }

        String[] descriptionParts = description.split("\\n", -1);
        return StreamSupport.stream(Arrays.spliterator(descriptionParts), false)
            .filter(part -> part.matches("^\\s*[Tt]itle:.*$"))
            .findFirst()
            .orElse("")
            .replaceFirst("[Tt]itle:", "")
            .trim();
    }
}
