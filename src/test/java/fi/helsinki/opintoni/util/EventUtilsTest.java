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

package fi.helsinki.opintoni.util;

import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.dto.EventDtoBuilder;
import fi.helsinki.opintoni.dto.LocationDto;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class EventUtilsTest {

    @Test
    public void thatCourseUriIsMergedCorrectly() {
        EventDto optimeEvent = new EventDtoBuilder()
            .setSource(EventDto.Source.OPTIME)
            .setTitle("Optime Event")
            .setRealisationId(123)
            .setLocations(new ArrayList<LocationDto>())
            .createEventDto();

        EventDto coursePageEvent = new EventDtoBuilder()
            .setSource(EventDto.Source.COURSE_PAGE)
            .setTitle("Course Page Event")
            .setRealisationId(123)
            .setCourseUri("http://coursepage/123")
            .setLocations(new ArrayList<LocationDto>())
            .createEventDto();

        EventDto expectedEvent = new EventDtoBuilder()
            .setSource(EventDto.Source.OPTIME)
            .setTitle("Optime Event")
            .setRealisationId(123)
            .setCourseUri("http://coursepage/123")
            .setLocations(new ArrayList<LocationDto>())
            .createEventDto();

        Stream<EventDto> optimeEventStream = Stream.of(optimeEvent);
        Stream<EventDto> coursePageEventStream = Stream.of(coursePageEvent);

        List<EventDto> mergedList = EventUtils.mergeStreams(optimeEventStream, coursePageEventStream);
        assertThat(mergedList.size()).isEqualTo(1);
        assertThat(mergedList.get(0).realisationId).isEqualTo(expectedEvent.realisationId);
        assertThat(mergedList.get(0).title).isEqualTo(expectedEvent.title);
        assertThat(mergedList.get(0).source).isEqualTo(expectedEvent.source);
        assertThat(mergedList.get(0).courseUri).isEqualTo(expectedEvent.courseUri);
    }
}
