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

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.dto.LocationDto;
import fi.helsinki.opintoni.integration.coursepage.CoursePageEvent;
import fi.helsinki.opintoni.integration.oodi.OodiEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class LocationResolver {

    private String getLocationString(OodiEvent event) {
        return Lists.newArrayList(event.roomName, event.buildingStreet).stream()
            .filter(Objects::nonNull)
            .collect(Collectors.joining(", "));
    }

    public LocationDto getLocation(OodiEvent oodiEvent) {
        return new LocationDto(
            getLocationString(oodiEvent),
            oodiEvent.roomName,
            oodiEvent.buildingStreet,
            oodiEvent.buildingZipCode
        );
    }

    public LocationDto getLocation(CoursePageEvent coursePageEvent) {
        String where;
        if (coursePageEvent.where != null) {
            where = coursePageEvent.where;
        } else {
            where = "";
        }

        return new LocationDto(where);
    }

}
