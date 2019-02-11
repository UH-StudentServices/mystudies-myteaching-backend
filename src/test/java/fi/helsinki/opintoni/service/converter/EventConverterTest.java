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

package fi.helsinki.opintoni.service.converter;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.sampledata.SampleDataFiles;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventConverterTest extends SpringTest {
    @Autowired
    private EventConverter eventConverter;

    @Value("classpath:sampledata/ICalendar/icalendar.ics")
    private Resource icalendar;

    @Test
    public void thatICalendarContentToEventsAndBackWorks() throws Exception {
        String iCalendarContent = SampleDataFiles.toText("ICalendar/icalendar.ics");

        List<EventDto> eventDtos = eventConverter.toDtos(icalendar.getInputStream());

        String converted = eventConverter.toICalendar(eventDtos);

        assertEquals(iCalendarContent, converted);
    }
}
