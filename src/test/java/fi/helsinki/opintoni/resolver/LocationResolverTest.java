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
import fi.helsinki.opintoni.integration.oodi.OodiEvent;
import fi.helsinki.opintoni.integration.oodi.OptimeExtras;
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
        OodiEvent oodiEvent = oodiEventWithLocation(ROOM, null, null);

        LocationDto locationDto = locationResolver.getLocation(oodiEvent);
        assertThat(locationDto).isNotNull();
        assertThat(locationDto.locationString).isEqualTo(ROOM);
        assertThat(locationDto.streetAddress).isNull();
    }

    @Test
    public void thatLocationIsReturnedWhenRoomIsNull() {
        OodiEvent oodiEvent = oodiEventWithLocation(null, STREET, null);

        LocationDto locationDto = locationResolver.getLocation(oodiEvent);
        assertThat(locationDto).isNotNull();
        assertThat(locationDto.locationString).isEqualTo(STREET);
        assertThat(locationDto.streetAddress).isEqualTo(STREET);
    }

    @Test
    public void thatEmptyLocationStringIsReturnedWhenEventFieldsAreNull() {
        OodiEvent oodiEvent = oodiEventWithLocation(null, null, null);

        LocationDto locationDto = locationResolver.getLocation(oodiEvent);
        assertThat(locationDto.locationString).isEqualTo("");
        assertThat(locationDto.roomName).isNull();
        assertThat(locationDto.streetAddress).isNull();
        assertThat(locationDto.zipCode).isNull();
    }

    @Test
    public void thatLocationIsReturnedFromOodi() {
        OodiEvent oodiEvent = oodiEventWithLocation(ROOM, STREET, ZIP);

        LocationDto locationDto = locationResolver.getLocation(oodiEvent);
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
    public void thatOodiEventWithOptimeDataCreatesTwoLocations() {
        OodiEvent oodiEventWithOptimeData = oodiEventWithLocation(ROOM, STREET, ZIP);
        oodiEventWithOptimeData.optimeExtras = optimeExtras(OTHER_NOTES, ROOM_NOTES, STAFF_NOTES);

        List<LocationDto> locations = locationResolver.getLocations(oodiEventWithOptimeData);
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

    private OodiEvent oodiEventWithLocation(String roomName, String street, String zip) {
        OodiEvent oodiEvent = new OodiEvent();
        oodiEvent.roomName = roomName;
        oodiEvent.buildingStreet = street;
        oodiEvent.buildingZipCode = zip;
        return oodiEvent;
    }

    private CoursePageEvent coursePageEventWithWhere(String where) {
        CoursePageEvent coursePageEvent = new CoursePageEvent();
        coursePageEvent.where = where;
        return coursePageEvent;
    }
}
