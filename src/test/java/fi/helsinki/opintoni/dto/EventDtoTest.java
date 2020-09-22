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

package fi.helsinki.opintoni.dto;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import fi.helsinki.opintoni.integration.IntegrationUtil;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventDtoTest {

    private static final String EVENT_TITLE = "Title of event";
    private static final String COURSE_TITLE = "Title of course";
    private static final String LOCATION = "Location";
    private static final String LOCATION_DETAIL = "Location details";
    private static final String CUR_ID = IntegrationUtil.getSisuCourseUnitRealisationId("100");

    @Test
    public void compareTo() {
        EventDto februaryCourse = new EventDtoBuilder()
            .setType(EventDto.Type.DEFAULT)
            .setSource(EventDto.Source.STUDY_REGISTRY)
            .setStartDate(LocalDateTime.of(2018, 2, 1, 8, 0))
            .setEndDate(LocalDateTime.of(2018, 2, 1, 8, 0))
            .setRealisationId(CUR_ID)
            .setTitle(EVENT_TITLE)
            .setCourseTitle(COURSE_TITLE)
            .setHasMaterial(false).createEventDto();

        EventDto januaryCourse = new EventDtoBuilder()
            .setType(EventDto.Type.DEFAULT)
            .setSource(EventDto.Source.STUDY_REGISTRY)
            .setStartDate(LocalDateTime.of(2018, 1, 1, 8, 0))
            .setEndDate(LocalDateTime.of(2018, 3, 1, 8, 0))
            .setRealisationId(CUR_ID)
            .setTitle(EVENT_TITLE)
            .setCourseTitle(COURSE_TITLE)
            .setHasMaterial(false).createEventDto();

        assertTrue(januaryCourse.compareTo(februaryCourse) < 0);
    }

    @Test
    public void getRealisationIdAndTimes() {
        EventDto eventDto = new EventDtoBuilder()
            .setType(EventDto.Type.DEFAULT)
            .setSource(EventDto.Source.COURSE_PAGE)
            .setStartDate(LocalDateTime.of(2018, 1, 1, 8, 0))
            .setEndDate(LocalDateTime.of(2018, 3, 1, 8, 0))
            .setRealisationId(CUR_ID)
            .setTitle(EVENT_TITLE)
            .setCourseTitle(COURSE_TITLE)
            .setHasMaterial(false).createEventDto();

        assertEquals("hy-CUR-1002018-01-01T08:002018-03-01T08:00", EventDto.getRealisationIdAndTimes(eventDto));
    }

    @Test
    public void getTitleWhenSourceIsStudyRegistry() {
        EventDto eventData = new EventDtoBuilder()
            .setType(EventDto.Type.DEFAULT)
            .setSource(EventDto.Source.STUDY_REGISTRY)
            .setStartDate(LocalDateTime.of(2018, 1, 1, 8, 0))
            .setEndDate(LocalDateTime.of(2018, 1, 1, 8, 0))
            .setRealisationId(CUR_ID)
            .setTitle(EVENT_TITLE)
            .setCourseTitle(COURSE_TITLE)
            .setHasMaterial(false).createEventDto();

        assertEquals(EVENT_TITLE, eventData.getFullEventTitle());
    }

    @Test
    public void getTitleWhenSourceIsCoursePage() {
        EventDto eventDto = new EventDtoBuilder()
            .setType(EventDto.Type.DEFAULT)
            .setSource(EventDto.Source.COURSE_PAGE)
            .setStartDate(LocalDateTime.of(2018, 1, 1, 8, 0))
            .setEndDate(LocalDateTime.of(2018, 1, 1, 8, 0))
            .setRealisationId(CUR_ID)
            .setTitle(EVENT_TITLE)
            .setCourseTitle(COURSE_TITLE)
            .setHasMaterial(false).createEventDto();

        assertEquals(String.format("%s, %s", EVENT_TITLE, COURSE_TITLE), eventDto.getFullEventTitle());
    }

    @Test
    public void getLocationsWithoutSeparatorIfEmptyLocationIsGiven() {
        EventDto eventDto = new EventDtoBuilder()
            .setLocations(ImmutableList.of(new LocationDto(LOCATION), new LocationDto("")))
            .createEventDto();
        assertEquals(LOCATION, eventDto.getLocationsAsString());

        EventDto secondEventDto = new EventDtoBuilder()
            .setLocations(ImmutableList.of(new LocationDto(""), new LocationDto(LOCATION)))
            .createEventDto();
        assertEquals(LOCATION, secondEventDto.getLocationsAsString());
    }
}
