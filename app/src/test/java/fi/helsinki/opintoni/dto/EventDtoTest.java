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

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventDtoTest {

    private static final String EVENT_TITLE = "Title of event";
    private static final String COURSE_TITLE = "Title of course";

    @Test
    public void compareTo() {
        EventDto februaryCourse = new EventDto(EventDto.Type.DEFAULT, EventDto.Source.COURSE_PAGE,
            LocalDateTime.of(2018, 2, 1, 8, 0),
            LocalDateTime.of(2018, 2, 1, 8, 0),
            100,
            EVENT_TITLE,
            COURSE_TITLE,
            "",
            "",
            null,
            "",
            false,
            null);
        EventDto januaryCourse = new EventDto(EventDto.Type.DEFAULT, EventDto.Source.COURSE_PAGE,
            LocalDateTime.of(2018, 1, 1, 8, 0),
            LocalDateTime.of(2018, 3, 1, 8, 0),
            100,
            EVENT_TITLE,
            COURSE_TITLE,
            "",
            "",
            null,
            "",
            false,
            null);

        assertTrue(januaryCourse.compareTo(februaryCourse) < 0);
    }

    @Test
    public void getRealisationIdAndTimes() {
        EventDto eventDto = new EventDto(EventDto.Type.DEFAULT, EventDto.Source.COURSE_PAGE,
            LocalDateTime.of(2018, 1, 1, 8, 0),
            LocalDateTime.of(2018, 3, 1, 8, 0),
            100,
            EVENT_TITLE,
            COURSE_TITLE,
            "",
            "",
            null,
            "",
            false,
            null);

        assertEquals("1002018-01-01T08:002018-03-01T08:00", EventDto.getRealisationIdAndTimes(eventDto));
    }

    @Test
    public void getTitleWhenSourceIsOodi() {
        EventDto eventDataFromOodi = new EventDto(EventDto.Type.DEFAULT, EventDto.Source.OODI,
            LocalDateTime.of(2018, 1, 1, 8, 0),
            LocalDateTime.of(2018, 1, 1, 8, 0),
            100,
            EVENT_TITLE,
            COURSE_TITLE,
            "",
            "",
            null,
            "",
            false,
            null);

        assertEquals(EVENT_TITLE, eventDataFromOodi.getFullEventTitle());
    }

    @Test
    public void getTitleWhenSourceIsCoursePage() {
        EventDto eventDataFromOodi = new EventDto(EventDto.Type.DEFAULT, EventDto.Source.COURSE_PAGE,
            LocalDateTime.of(2018, 1, 1, 8, 0),
            LocalDateTime.of(2018, 1, 1, 8, 0),
            100,
            EVENT_TITLE,
            COURSE_TITLE,
            "",
            "",
            null,
            "",
            false,
            null);

        assertEquals(String.format("%s, %s", EVENT_TITLE, COURSE_TITLE), eventDataFromOodi.getFullEventTitle());
    }

}
