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

package fi.helsinki.opintoni.service;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.domain.CalendarFeed;
import fi.helsinki.opintoni.dto.CalendarFeedDto;
import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.exception.http.CalendarFeedNotFoundException;
import fi.helsinki.opintoni.integration.studyregistry.Person;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryService;
import fi.helsinki.opintoni.util.TimeZoneUtils;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static fi.helsinki.opintoni.service.TimeService.HELSINKI_ZONE_ID;

@Service
public class CalendarService {

    private final EventService eventService;
    private final StudyRegistryService studyRegistryService;
    private final CalendarTransactionalService calendarTransactionalService;
    private final TimeService timeService;
    private final TimeZoneUtils timeZoneUtils;

    @Autowired
    public CalendarService(EventService eventService,
                           StudyRegistryService studyRegistryService,
                           CalendarTransactionalService calendarTransactionalService,
                           TimeService timeService,
                           TimeZoneUtils timeZoneUtils) {

        this.eventService = eventService;
        this.studyRegistryService = studyRegistryService;
        this.calendarTransactionalService = calendarTransactionalService;
        this.timeService = timeService;
        this.timeZoneUtils = timeZoneUtils;
    }

    public CalendarFeedDto getCalendarFeed(Long userId) {
        return calendarTransactionalService.getCalendarFeed(userId);
    }

    public CalendarFeedDto createCalendarFeed(Long userId) {
        return calendarTransactionalService.createCalendarFeed(userId);
    }

    public String showCalendarFeed(String feedId, Locale locale) {
        return calendarTransactionalService.findByFeedId(feedId)
            .map(c -> getCalendarFeedFromEvents(c, locale))
            .orElseThrow(CalendarFeedNotFoundException::new);
    }

    private String getCalendarFeedFromEvents(CalendarFeed calendarFeed, Locale locale) {

        VTimeZone timeZone = timeZoneUtils.getHelsinkiTimeZone();

        Calendar calendar = new Calendar();
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        calendar.getComponents().add(timeZone);

        Person person = studyRegistryService.getPerson(calendarFeed.user.personId);

        List<EventDto> studentEvents = Optional.ofNullable(person.studentNumber)
            .map(s -> eventService.getStudentEvents(s, locale))
            .orElse(Lists.newArrayList());
        List<EventDto> teacherEvents = Optional.ofNullable(person.teacherNumber)
            .map(s -> eventService.getTeacherEvents(s, locale))
            .orElse(Lists.newArrayList());

        studentEvents.addAll(teacherEvents);

        studentEvents.forEach(e -> {
            VEvent event = eventDtoToVEvent(e);
            calendar.getComponents().add(event);
        });

        return calendar.toString();
    }

    private Uid generateUid() {
        return new Uid(UUID.randomUUID().toString());
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
        eventProperties.add(generateUid());

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
}
