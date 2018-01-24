package fi.helsinki.opintoni.dto;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class EventDtoTest {

    @Test
    public void compareTo() {
        EventDto februaryCourse = new EventDto(EventDto.Type.DEFAULT, EventDto.Source.COURSE_PAGE,
            LocalDateTime.of(2018, 2, 1, 8, 0),
            LocalDateTime.of(2018, 2, 1, 8, 0),
            100,
            "Title of event",
            "Title of course",
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
            "Title of event",
            "Title of course",
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
            "Title of event",
            "Title of course",
            "",
            "",
            null,
            "",
            false,
            null);
        
        assertEquals("1002018-01-01T08:002018-03-01T08:00", EventDto.getRealisationIdAndTimes(eventDto));
    }

    @Test
    public void getTitle_whenSourceIsOodi() {
        final String titleGeneratedFromOodi = "Generated title from oodi";
        EventDto eventDataFromOodi = new EventDto(EventDto.Type.DEFAULT, EventDto.Source.OODI,
            LocalDateTime.of(2018, 1, 1, 8, 0),
            LocalDateTime.of(2018, 1, 1, 8, 0),
            100,
            titleGeneratedFromOodi,
            "CourseTitle",
            "",
            "",
            null,
            "",
            false,
            null);

        assertEquals(titleGeneratedFromOodi, eventDataFromOodi.getTitle());
    }

    @Test
    public void getTitle_whenSourceIsCoursePage() {
        final String eventTitle = "EventTitle";
        final String courseTitle = "CourseTitle";
        EventDto eventDataFromOodi = new EventDto(EventDto.Type.DEFAULT, EventDto.Source.COURSE_PAGE,
            LocalDateTime.of(2018, 1, 1, 8, 0),
            LocalDateTime.of(2018, 1, 1, 8, 0),
            100,
            eventTitle,
            courseTitle,
            "",
            "",
            null,
            "",
            false,
            null);

        assertEquals(String.format("%s, %s", eventTitle, courseTitle), eventDataFromOodi.getTitle());
    }

}
