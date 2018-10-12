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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationResolverTest extends SpringTest {
    private static String room = "Huone";
    private static String street = "Osoite";
    private static String zip = "1345";

    @Autowired
    private LocationResolver locationResolver;

    @Test
    public void thatLocationIsReturnedWhenStreetIsNull() {
        OodiEvent oodiEvent = oodiEventWithLocation(room, null, null);

        LocationDto locationDto = locationResolver.getLocation(oodiEvent);
        assertThat(locationDto).isNotNull();
        assertThat(locationDto.locationString).isEqualTo(room);
        assertThat(locationDto.streetAddress).isNull();
    }

    @Test
    public void thatLocationIsReturnedWhenRoomIsNull() {
        OodiEvent oodiEvent = oodiEventWithLocation(null, street, null);

        LocationDto locationDto = locationResolver.getLocation(oodiEvent);
        assertThat(locationDto).isNotNull();
        assertThat(locationDto.locationString).isEqualTo(street);
        assertThat(locationDto.streetAddress).isEqualTo(street);
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
        OodiEvent oodiEvent = oodiEventWithLocation(room, street, zip);

        LocationDto locationDto = locationResolver.getLocation(oodiEvent);
        assertThat(locationDto.locationString).isEqualTo(room + ", " + street);
        assertThat(locationDto.roomName).isEqualTo(room);
        assertThat(locationDto.streetAddress).isEqualTo(street);
        assertThat(locationDto.zipCode).isEqualTo(zip);
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
