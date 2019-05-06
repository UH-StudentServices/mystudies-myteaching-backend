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

package fi.helsinki.opintoni.resolver;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.LocationDto;
import fi.helsinki.opintoni.integration.coursepage.CoursePageEvent;
import fi.helsinki.opintoni.integration.studyregistry.Event;
import fi.helsinki.opintoni.integration.studyregistry.OptimeExtras;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationResolverTest extends SpringTest {
    private static final String ROOM = "Huone";
    private static final String STREET = "Osoite";
    private static final String ZIP = "1345";
    private static final String ROOM_NOTES = "ROOM_NOTES";
    private static final String OTHER_NOTES = "OTHER_NOTES";
    private static final String STAFF_NOTES = "STAFF_NOTES";

    @Autowired
    private LocationResolver locationResolver;

    @Test
    public void thatLocationIsReturnedWhenStreetIsNull() {
        Event event = eventWithLocation(ROOM, null, null);

        LocationDto locationDto = locationResolver.getLocation(event);
        assertThat(locationDto).isNotNull();
        assertThat(locationDto.locationString).isEqualTo(ROOM);
        assertThat(locationDto.streetAddress).isNull();
    }

    @Test
    public void thatLocationIsReturnedWhenRoomIsNull() {
        Event event = eventWithLocation(null, STREET, null);

        LocationDto locationDto = locationResolver.getLocation(event);
        assertThat(locationDto).isNotNull();
        assertThat(locationDto.locationString).isEqualTo(STREET);
        assertThat(locationDto.streetAddress).isEqualTo(STREET);
    }

    @Test
    public void thatEmptyLocationStringIsReturnedWhenEventFieldsAreNull() {
        Event event = eventWithLocation(null, null, null);

        LocationDto locationDto = locationResolver.getLocation(event);
        assertThat(locationDto.locationString).isEqualTo("");
        assertThat(locationDto.roomName).isNull();
        assertThat(locationDto.streetAddress).isNull();
        assertThat(locationDto.zipCode).isNull();
    }

    @Test
    public void thatLocationIsReturnedFromOodi() {
        Event event = eventWithLocation(ROOM, STREET, ZIP);

        LocationDto locationDto = locationResolver.getLocation(event);
        assertThat(locationDto.locationString).isEqualTo(ROOM + ", " + STREET);
        assertThat(locationDto.roomName).isEqualTo(ROOM);
        assertThat(locationDto.streetAddress).isEqualTo(STREET);
        assertThat(locationDto.zipCode).isEqualTo(ZIP);
    }

    private void coursePageEventStringTest(String where) {
        CoursePageEvent coursePageEvent = coursePageEventWithWhere(where);

        LocationDto locationDto = locationResolver.getLocation(coursePageEvent);
        assertThat(locationDto.locationString).isEqualTo(where);
        assertThat(locationDto.roomName).isEqualTo(where);
        assertThat(locationDto.streetAddress).isNull();
        assertThat(locationDto.zipCode).isNull();
    }

    @Test
    public void thatLocationIsReturnedFromCoursePage() {
        coursePageEventStringTest("Luentosali 2");
    }

    @Test
    public void thatEmptyLocationIsReturnedFromCoursePageWithEmptyWhere() {
        coursePageEventStringTest("");
    }

    @Test
    public void thatEmptyLocationIsReturnedFromCoursePageWithNull() {
        CoursePageEvent coursePageEvent = coursePageEventWithWhere(null);

        LocationDto locationDto = locationResolver.getLocation(coursePageEvent);
        assertThat(locationDto.locationString).isEqualTo("");
        assertThat(locationDto.roomName).isEqualTo("");
        assertThat(locationDto.streetAddress).isNull();
        assertThat(locationDto.zipCode).isNull();
    }

    @Test
    public void thatEventWithOptimeDataCreatesTwoLocations() {
        Event eventWithOptimeData = eventWithLocation(ROOM, STREET, ZIP);
        eventWithOptimeData.optimeExtras = optimeExtras(OTHER_NOTES, ROOM_NOTES, STAFF_NOTES);

        List<LocationDto> locations = locationResolver.getLocations(eventWithOptimeData);
        assertThat(locations).hasSize(2);
        assertThat(locations.get(0).locationString).isEqualTo(ROOM + ", " + STREET);
        assertThat(locations.get(0).roomName).isEqualTo(ROOM);
        assertThat(locations.get(0).streetAddress).isEqualTo(STREET);
        assertThat(locations.get(0).zipCode).isEqualTo(ZIP);
        assertThat(locations.get(1).locationString).isEqualTo(ROOM_NOTES);
        assertThat(locations.get(1).roomName).isEqualTo(ROOM_NOTES);
        assertThat(locations.get(1).streetAddress).isNull();
        assertThat(locations.get(1).zipCode).isNull();
    }

    private OptimeExtras optimeExtras(String otherNotes, String roomNotes, String staffNotes) {
        OptimeExtras optimeExtras = new OptimeExtras();
        optimeExtras.roomNotes = roomNotes;
        optimeExtras.otherNotes = otherNotes;
        optimeExtras.staffNotes = staffNotes;
        return optimeExtras;
    }

    private Event eventWithLocation(String roomName, String street, String zip) {
        Event event = new Event();
        event.roomName = roomName;
        event.buildingStreet = street;
        event.buildingZipCode = zip;
        return event;
    }

    private CoursePageEvent coursePageEventWithWhere(String where) {
        CoursePageEvent coursePageEvent = new CoursePageEvent();
        coursePageEvent.where = where;
        return coursePageEvent;
    }
}
