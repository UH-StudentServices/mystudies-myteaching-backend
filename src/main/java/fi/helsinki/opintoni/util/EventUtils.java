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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventUtils {

    public static List<EventDto> mergeStreams(Stream<EventDto> s1, Stream<EventDto> s2) {
        return Stream
            .concat(s1, s2)
            .collect(Collectors.toMap(EventDto::getRealisationIdAndTimes, Function.identity(), (a, b) -> new EventDtoBuilder()
                .setType(a.type)
                .setSource(getEventSource(a, b))
                .setStartDate(a.startDate)
                .setEndDate(a.endDate)
                .setRealisationId(a.realisationId)
                .setTitle(a.title)
                .setCourseTitle(a.courseTitle)
                .setCourseUri(a.courseUri != null ? a.courseUri : b.courseUri)
                .setCourseImageUri(a.courseImageUri)
                .setCourseMaterialDto(a.courseMaterial)
                .setMoodleUri(a.moodleUri)
                .setHasMaterial(a.hasMaterial)
                .setLocations(Stream.concat(a.locations.stream(), b.locations.stream()).collect(Collectors.toList()))
                .setOptimeExtras(a.optimeExtras != null ? a.optimeExtras : b.optimeExtras)
                .createEventDto())).values().stream()
            .sorted()
            .collect(Collectors.toList());
    }

    private static EventDto.Source getEventSource(EventDto lhs, EventDto rhs) {
        if (lhs.source == EventDto.Source.STUDY_REGISTRY || rhs.source == EventDto.Source.STUDY_REGISTRY) {
            return EventDto.Source.STUDY_REGISTRY;
        } else if (lhs.source == EventDto.Source.OPTIME || rhs.source == EventDto.Source.OPTIME) {
            return EventDto.Source.OPTIME;
        }
        return EventDto.Source.COURSE_PAGE;
    }
}
