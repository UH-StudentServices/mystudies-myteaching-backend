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

package fi.helsinki.opintoni.web.rest.publicapi;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Test;
import org.mockito.internal.matchers.And;
import org.mockito.internal.matchers.EndsWith;
import org.mockito.internal.matchers.StartsWith;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicCalendarFeedResourceTest extends SpringTest {

    private static final String CRLF = "\r\n";

    @Test
    public void thatTheFeedIsDisplayed() throws Exception {
        expectEvents();

        String expectedICalStart = String.join(CRLF,
            "BEGIN:VCALENDAR",
                "VERSION:2.0",
                "CALSCALE:GREGORIAN",
                "BEGIN:VTIMEZONE",
                "TZID:Europe/Helsinki",
                "TZURL:http://tzurl.org/zoneinfo/Europe/Helsinki",
                "X-LIC-LOCATION:Europe/Helsinki");

        String expectedICalEnd = String.join(CRLF,
                "BEGIN:VEVENT",
                "DTSTART:20141027T121500Z",
                "DTEND:20141027T134500Z",
                "SUMMARY:Johdatus kasvatuspsykologiaan\\, Animal Biotechnology B (KEL/KEBIOT230)",
                "LOCATION:Päärakennus\\, sali 1\\, Viikinkaari 11",
                "END:VEVENT",
                "BEGIN:VEVENT",
                "DTSTART:20141028T140000Z",
                "DTEND:20141128T140000Z",
                "SUMMARY:Title: Test 04159adb2253\\, Animal Biotechnology B (KEL/KEBIOT230)",
                "LOCATION:Place: Test 04156f654df1",
                "END:VEVENT",
                "BEGIN:VEVENT",
                "DTSTART:20150831T090000Z",
                "DTEND:20150831T120000Z",
                "SUMMARY:Formulointi III - tentti\\, Animal Biotechnology B (KEL/KEBIOT230)",
                "LOCATION:Arppeanumin auditorio\\, Viikinkaari 11",
                "END:VEVENT",
                "BEGIN:VEVENT",
                "DTSTART:20401028T110000Z",
                "DTEND:20401128T140000Z",
                "SUMMARY:Tentti\\, Animal Biotechnology B (KEL/KEBIOT230)",
                "LOCATION:Tenttisali",
                "END:VEVENT",
                "BEGIN:VEVENT",
                "DTSTART:20401028T110000Z",
                "DTEND:20401028T215959Z",
                "SUMMARY:Ei päättymisaikaa\\, Animal Biotechnology B (KEL/KEBIOT230)",
                "LOCATION:Paikka X",
                "END:VEVENT",
                "END:VCALENDAR",
                "");

        mockMvc.perform(get("/api/public/v1/calendar/c9ea7949-577c-458c-a9d9-3c2a39269dd8/en"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.TEXT_CALENDAR_UTF8))
            .andExpect(content().string(new And(newArrayList(new StartsWith(expectedICalStart),
                new EndsWith(expectedICalEnd)))));
    }

    private void expectEvents() {
        defaultStudentRequestChain()
            .roles("roleswithstudentrole.json")
            .events()
            .defaultImplementation()
            .and()
            .enrollments()
            .defaultCourseUnitRealisation()
            .defaultOneOffEvents();
    }
}
