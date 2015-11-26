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

import fi.helsinki.opintoni.domain.CalendarFeed;
import fi.helsinki.opintoni.dto.CalendarFeedDto;
import fi.helsinki.opintoni.repository.CalendarFeedRepository;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.service.converter.CalendarFeedConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class CalendarTransactionalService extends DtoService {

    private final CalendarFeedConverter calendarFeedConverter;
    private final UserRepository userRepository;
    private final CalendarFeedRepository calendarFeedRepository;

    @Autowired
    public CalendarTransactionalService(CalendarFeedConverter calendarFeedConverter,
                                        UserRepository userRepository,
                                        CalendarFeedRepository calendarFeedRepository) {

        this.calendarFeedConverter = calendarFeedConverter;
        this.userRepository = userRepository;
        this.calendarFeedRepository = calendarFeedRepository;
    }

    public CalendarFeedDto getCalendarFeed(Long userId) {
        return getDto(userId, calendarFeedRepository::findByUserId, calendarFeedConverter::toDto);
    }

    public CalendarFeedDto createCalendarFeed(Long userId) {
        CalendarFeed calendarFeed = new CalendarFeed();
        calendarFeed.user = userRepository.findOne(userId);
        calendarFeed.feedId = UUID.randomUUID().toString();
        return calendarFeedConverter.toDto(calendarFeedRepository.save(calendarFeed));
    }

    public void deleteCalendarFeed(Long userId) {
        calendarFeedRepository.deleteByUserId(userId);
    }

    public Optional<CalendarFeed> findByFeedId(String feedId) {
        return calendarFeedRepository.findByFeedId(feedId);
    }
}
