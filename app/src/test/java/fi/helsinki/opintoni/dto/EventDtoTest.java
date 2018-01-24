package fi.helsinki.opintoni.dto;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;

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

        assertEquals(EVENT_TITLE, eventDataFromOodi.getTitle());
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

        assertEquals(String.format("%s, %s", EVENT_TITLE, COURSE_TITLE), eventDataFromOodi.getTitle());
    }

}
