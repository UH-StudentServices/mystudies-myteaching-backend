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
import fi.helsinki.opintoni.integration.DateFormatter;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiRoles;
import fi.helsinki.opintoni.util.TimeZoneUtils;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class CalendarService {

    private final EventService eventService;
    private final OodiClient oodiClient;
    private final CalendarTransactionalService calendarTransactionalService;
    private final TimeService timeService;
    private final TimeZoneUtils timeZoneUtils;

    @Autowired
    public CalendarService(EventService eventService,
                           OodiClient oodiClient,
                           CalendarTransactionalService calendarTransactionalService,
                           TimeService timeService,
                           TimeZoneUtils timeZoneUtils) {

        this.eventService = eventService;
        this.oodiClient = oodiClient;
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

        OodiRoles oodiRoles = oodiClient.getRoles(calendarFeed.user.oodiPersonId);

        List<EventDto> studentEvents = Optional.ofNullable(oodiRoles.studentNumber)
            .map(s -> eventService.getStudentEvents(s, locale))
            .orElse(Lists.newArrayList());
        List<EventDto> teacherEvents = Optional.ofNullable(oodiRoles.teacherNumber)
            .map(s -> eventService.getTeacherEvents(s, locale))
            .orElse(Lists.newArrayList());

        studentEvents.addAll(teacherEvents);

        studentEvents.stream().forEach(e -> {
            VEvent event = eventDtoToVEvent(e);
            calendar.getComponents().add(event);
        });

        return calendar.toString();
    }

    private String getEventTitle(EventDto eventDto) {
        String title = eventDto.title;
        if (eventDto.courseTitle != null) {
            title += ", " + eventDto.courseTitle;
        }
        return title;
    }

    private Uid generateUid() {
        return new Uid(UUID.randomUUID().toString());
    }

    private VEvent eventDtoToVEvent(EventDto eventDto) {
        PropertyList eventProperties = new PropertyList();
        eventProperties.add(new DtStart(convertStartDateToCalDate(eventDto)));
        eventProperties.add(new DtEnd(convertEndDateToCalDate(eventDto)));
        eventProperties.add(new Summary(getEventTitle(eventDto)));
        eventProperties.add(new Location((eventDto.locations)));
        eventProperties.add(generateUid());

        return new VEvent(eventProperties);
    }

    private DateTime convertStartDateToCalDate(EventDto eventDto) {
        return calDateTimeAsUtc(eventDto.startDate);
    }

    private DateTime calDateTimeAsUtc(LocalDateTime localDateTime) {
        String utc = localDateTime.format(DateTimeFormatter.ofPattern(DateFormatter.UTC_TIME_FORMAT));
        try {
            return new DateTime(utc, DateFormatter.UTC_TIME_FORMAT, true);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private DateTime convertEndDateToCalDate(EventDto eventDto) {
        if (eventDto.endDate == null) {
            LocalDateTime endOfDayHelsinki = timeService.endOfDayHelsinki(eventDto.startDate);
            return calDateTimeAsUtc(endOfDayHelsinki);
        }

        return calDateTimeAsUtc(eventDto.endDate);
    }
}
