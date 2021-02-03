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

package fi.helsinki.opintoni.integration.optime;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.service.converter.EventConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Service
public class OptimeService {

    private final EventConverter eventConverter;
    private final OptimeClient optimeClient;

    @Autowired
    public OptimeService(EventConverter eventConverter,
                         OptimeClient optimeClient) {
        this.eventConverter = eventConverter;
        this.optimeClient = optimeClient;
    }

    public InputStream getICalendarContent(String feedUrl) {
        return optimeClient.getICalendarContent(feedUrl);
    }

    /**
     * Fetches Optime events from given URL.
     *
     * @param feedUrl is URL to Optime Calendar.
     * @return fetched events and if given URL is null or blank then return empty list.
     */
    @Cacheable(value = CacheConstants.TEACHER_EVENTS, cacheManager = "transientCacheManager")
    public List<EventDto> getOptimeEvents(String feedUrl) {
        if (feedUrl == null) {
            return Collections.emptyList();
        }
        return eventConverter.toDtos(getICalendarContent(feedUrl));
    }
}
