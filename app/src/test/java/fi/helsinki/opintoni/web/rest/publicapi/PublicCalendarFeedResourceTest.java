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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicCalendarFeedResourceTest extends SpringTest {

    @Test
    public void thatTheFeedIsDisplayed() throws Exception {
        expectEvents();

        String expectedResult =
            "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "CALSCALE:GREGORIAN\r\n" +
                "BEGIN:VTIMEZONE\r\n" +
                "TZID:Europe/Helsinki\r\n" +
                "TZURL:http://tzurl.org/zoneinfo/Europe/Helsinki\r\n" +
                "X-LIC-LOCATION:Europe/Helsinki\r\n" +
                "BEGIN:DAYLIGHT\r\n" +
                "TZOFFSETFROM:+0200\r\n" +
                "TZOFFSETTO:+0300\r\n" +
                "TZNAME:EEST\r\n" +
                "DTSTART:19830327T030000\r\n" +
                "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU\r\n" +
                "END:DAYLIGHT\r\n" +
                "BEGIN:STANDARD\r\n" +
                "TZOFFSETFROM:+0300\r\n" +
                "TZOFFSETTO:+0200\r\n" +
                "TZNAME:EET\r\n" +
                "DTSTART:19961027T040000\r\n" +
                "RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU\r\n" +
                "END:STANDARD\r\n" +
                "BEGIN:STANDARD\r\n" +
                "TZOFFSETFROM:+013949\r\n" +
                "TZOFFSETTO:+013949\r\n" +
                "TZNAME:HMT\r\n" +
                "DTSTART:18780531T000000\r\n" +
                "RDATE:18780531T000000\r\n" +
                "END:STANDARD\r\n" +
                "BEGIN:STANDARD\r\n" +
                "TZOFFSETFROM:+013949\r\n" +
                "TZOFFSETTO:+0200\r\n" +
                "TZNAME:EET\r\n" +
                "DTSTART:19210501T002011\r\n" +
                "RDATE:19210501T002011\r\n" +
                "END:STANDARD\r\n" +
                "BEGIN:DAYLIGHT\r\n" +
                "TZOFFSETFROM:+0200\r\n" +
                "TZOFFSETTO:+0300\r\n" +
                "TZNAME:EEST\r\n" +
                "DTSTART:19420402T235959\r\n" +
                "RDATE:19420402T235959\r\n" +
                "RDATE:19810329T020000\r\n" +
                "RDATE:19820328T020000\r\n" +
                "END:DAYLIGHT\r\n" +
                "BEGIN:STANDARD\r\n" +
                "TZOFFSETFROM:+0300\r\n" +
                "TZOFFSETTO:+0200\r\n" +
                "TZNAME:EET\r\n" +
                "DTSTART:19421004T010000\r\n" +
                "RDATE:19421004T010000\r\n" +
                "RDATE:19810927T030000\r\n" +
                "RDATE:19820926T030000\r\n" +
                "RDATE:19830925T040000\r\n" +
                "RDATE:19840930T040000\r\n" +
                "RDATE:19850929T040000\r\n" +
                "RDATE:19860928T040000\r\n" +
                "RDATE:19870927T040000\r\n" +
                "RDATE:19880925T040000\r\n" +
                "RDATE:19890924T040000\r\n" +
                "RDATE:19900930T040000\r\n" +
                "RDATE:19910929T040000\r\n" +
                "RDATE:19920927T040000\r\n" +
                "RDATE:19930926T040000\r\n" +
                "RDATE:19940925T040000\r\n" +
                "RDATE:19950924T040000\r\n" +
                "END:STANDARD\r\n" +
                "BEGIN:STANDARD\r\n" +
                "TZOFFSETFROM:+0200\r\n" +
                "TZOFFSETTO:+0200\r\n" +
                "TZNAME:EET\r\n" +
                "DTSTART:19830101T000000\r\n" +
                "RDATE:19830101T000000\r\n" +
                "END:STANDARD\r\n" +
                "END:VTIMEZONE\r\n" +
                "BEGIN:VEVENT\r\n" +
                "DTSTART:20141027T121500Z\r\n" +
                "DTEND:20141027T134500Z\r\n" +
                "SUMMARY:Johdatus kasvatuspsykologiaan\\, Animal Biotechnology B (KEL/KEBIOT230)\r\n" +
                "LOCATION:Päärakennus\\, sali 1\\, Viikinkaari 11\r\n" +
                "END:VEVENT\r\n" +
                "BEGIN:VEVENT\r\n" +
                "DTSTART:20141028T140000Z\r\n" +
                "DTEND:20141128T140000Z\r\n" +
                "SUMMARY:Title: Test 04159adb2253\\, Animal Biotechnology B (KEL/KEBIOT230)\r\n" +
                "LOCATION:Place: Test 04156f654df1\r\n" +
                "END:VEVENT\r\n" +
                "BEGIN:VEVENT\r\n" +
                "DTSTART:20150831T090000Z\r\n" +
                "DTEND:20150831T120000Z\r\n" +
                "SUMMARY:Formulointi III - tentti\\, Animal Biotechnology B (KEL/KEBIOT230)\r\n" +
                "LOCATION:Arppeanumin auditorio\\, Viikinkaari 11\r\n" +
                "END:VEVENT\r\n" +
                "BEGIN:VEVENT\r\n" +
                "DTSTART:20401028T110000Z\r\n" +
                "DTEND:20401128T140000Z\r\n" +
                "SUMMARY:Tentti\\, Animal Biotechnology B (KEL/KEBIOT230)\r\n" +
                "LOCATION:Tenttisali\r\n" +
                "END:VEVENT\r\n" +
                "BEGIN:VEVENT\r\n" +
                "DTSTART:20401028T110000Z\r\n" +
                "DTEND:20401028T215959Z\r\n" +
                "SUMMARY:Ei päättymisaikaa\\, Animal Biotechnology B (KEL/KEBIOT230)\r\n" +
                "LOCATION:Paikka X\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n";

        mockMvc.perform(get("/api/public/v1/calendar/c9ea7949-577c-458c-a9d9-3c2a39269dd8/en"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.TEXT_CALENDAR_UTF8))
            .andExpect(content().string(expectedResult));
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
