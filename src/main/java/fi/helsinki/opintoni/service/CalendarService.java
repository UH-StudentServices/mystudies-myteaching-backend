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
import fi.helsinki.opintoni.service.converter.EventConverter;
import fi.helsinki.opintoni.util.TimeZoneUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class CalendarService {

    private final EventService eventService;
    private final StudyRegistryService studyRegistryService;
    private final CalendarTransactionalService calendarTransactionalService;
    private final TimeZoneUtils timeZoneUtils;
    private final EventConverter eventConverter;

    @Autowired
    public CalendarService(EventService eventService,
                           StudyRegistryService studyRegistryService,
                           CalendarTransactionalService calendarTransactionalService,
                           TimeZoneUtils timeZoneUtils,
                           EventConverter eventConverter) {

        this.eventService = eventService;
        this.studyRegistryService = studyRegistryService;
        this.calendarTransactionalService = calendarTransactionalService;
        this.timeZoneUtils = timeZoneUtils;
        this.eventConverter = eventConverter;
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
        Person person = studyRegistryService.getPerson(calendarFeed.user.personId);

        List<EventDto> studentEvents = Optional.ofNullable(person.studentNumber)
            .map(s -> eventService.getStudentEvents(s, locale))
            .orElse(Lists.newArrayList());
        List<EventDto> teacherEvents = Optional.ofNullable(person.teacherNumber)
            .map(s -> eventService.getTeacherEvents(s, locale))
            .orElse(Lists.newArrayList());

        studentEvents.addAll(teacherEvents);

        return eventConverter.toICalendar(studentEvents);
    }
}
