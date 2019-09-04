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
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventConverterTest extends SpringTest {

    @Autowired
    private EventConverter eventConverter;

    @Value("classpath:sampledata/ICalendar/icalendar.ics")
    private Resource icalendar;

    @Test
    public void thatConvertingICalendarContentToEventsAndBackWorks() throws Exception {

        String icalendarContent = SampleDataFiles.toText("ICalendar/icalendar.ics").replaceAll("\n", "\r\n");

        CalendarBuilder builder = new CalendarBuilder();

        Calendar calendar = builder.build(new ByteArrayInputStream(icalendarContent.getBytes(Charset.forName("UTF-8"))));

        List<EventDto> eventDtos = eventConverter.toDtos(icalendar.getInputStream());

        String converted = eventConverter.toICalendar(eventDtos);

        assertThat(converted).isEqualTo(calendar.toString());
    }

    @Test
    public void thatParsingOptimeEventDescriptionToOptimeExtraInfoWorks() {
        PropertyList<Property> eventProperties = new PropertyList<>();
        eventProperties.add(new Description("testing optime extras parsing\n\n*extra info\n*more info\n*a bit more"));
        eventProperties.add(new DtStart(new DateTime()));
        eventProperties.add(new DtEnd(new DateTime()));
        eventProperties.add(new Summary(""));
        eventProperties.add(new Location(""));
        eventProperties.add(new Uid(""));
        VEvent event = new VEvent(eventProperties);

        EventDto eventDto = eventConverter.toDto(event);

        assertThat(eventDto.optimeExtras.toString()).isEqualTo("*extra info, *more info, *a bit more");
    }
}
