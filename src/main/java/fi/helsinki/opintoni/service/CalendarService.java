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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final EventService eventService;
    private final StudyRegistryService studyRegistryService;
    private final CalendarTransactionalService calendarTransactionalService;
    private final EventConverter eventConverter;

    private static final Logger log = LoggerFactory.getLogger(CalendarService.class);

    @Autowired
    public CalendarService(EventService eventService,
                           StudyRegistryService studyRegistryService,
                           CalendarTransactionalService calendarTransactionalService,
                           EventConverter eventConverter) {
        this.eventService = eventService;
        this.studyRegistryService = studyRegistryService;
        this.calendarTransactionalService = calendarTransactionalService;
        this.eventConverter = eventConverter;
    }

    public CalendarFeedDto getCalendarFeed(Long userId) {
        return calendarTransactionalService.getCalendarFeed(userId);
    }

    public CalendarFeedDto createOrUpdateCalendarFeed(Long userId) {
        return calendarTransactionalService.createOrUpdateCalendarFeed(userId);
    }

    public String showCalendarFeed(String feedId, Locale locale) {
        return calendarTransactionalService.findByFeedId(feedId)
            .map(c -> getCalendarFeedFromEvents(c, locale))
            .orElseThrow(CalendarFeedNotFoundException::new);
    }

    private String getCalendarFeedFromEvents(CalendarFeed calendarFeed, Locale locale) {
        log.warn(String.format("Calendar feed should be fetched from Optime, but now fetching from Opetukseni for user %s: %s.",
                calendarFeed.user.personId, calendarFeed.user.eduPersonPrincipalName));
        Optional<Person> person = Optional.ofNullable(
            studyRegistryService.getPerson(calendarFeed.user.personId)
        );

        List<EventDto> events = person.stream().map(p -> Optional.ofNullable(p.teacherNumber)
                .map(s -> eventService.getTeacherEvents(calendarFeed.user.personId, locale))
                .orElse(Lists.newArrayList())).flatMap(List::stream).collect(Collectors.toList());

        return eventConverter.toICalendar(events);

    }
}
