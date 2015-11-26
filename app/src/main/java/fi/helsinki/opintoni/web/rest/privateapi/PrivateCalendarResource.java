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

package fi.helsinki.opintoni.web.rest.privateapi;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.dto.CalendarFeedDto;
import fi.helsinki.opintoni.service.CalendarService;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = RestConstants.PRIVATE_API_V1 + "/calendar")
public class PrivateCalendarResource extends AbstractResource {

    private final CalendarService calendarService;

    @Autowired
    public PrivateCalendarResource(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Timed
    public ResponseEntity<CalendarFeedDto> getCalendarFeed(@UserId Long userId) throws Exception {
        return response(calendarService.getCalendarFeed(userId));
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    public ResponseEntity<CalendarFeedDto> createCalendarFeed(@UserId Long userId) throws Exception {
        return response(calendarService.createCalendarFeed(userId));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Timed
    public ResponseEntity<Void> deleteCalendarFeed(@UserId Long userId) throws Exception {
        calendarService.deleteCalendarFeed(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
