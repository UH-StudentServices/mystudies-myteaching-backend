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
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.web.WebConstants;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringEndsWith;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicCalendarFeedResourceTest extends SpringTest {

    private static final String CRLF = "\r\n";

    @Test
    public void thatCalendarFeedIsDisplayed() throws Exception {
        Language language = Language.EN;

        expectEvents(language);

        String expectedFeedStart = String.join(CRLF,
            "BEGIN:VCALENDAR",
                "VERSION:2.0",
                "CALSCALE:GREGORIAN",
                "BEGIN:VTIMEZONE",
                "TZID:Europe/Helsinki",
                "TZURL:http://tzurl.org/zoneinfo/Europe/Helsinki",
                "X-LIC-LOCATION:Europe/Helsinki");

        List<String> expectedCalendarEvents = newArrayList(
            eventToString(
                "BEGIN:VEVENT",
                "DTSTART;TZID=Europe/Helsinki:20161219T141500",
                "DTEND;TZID=Europe/Helsinki:20161219T154500",
                "SUMMARY:Formulat... Harjoitus II (en)",
                "DESCRIPTION:Aku Ankka\\, testauksessa mukana",
                "LOCATION:Päärakennus\\, sali 1\\, Viikinkaari 11\\, Päärakennus\\, "
                    + "sali 2\\, Viikinkaari 11\\, Päärakennus\\, sali 3\\, Viikinkaari 11",
                "UID:"),
            eventToString(
                "BEGIN:VEVENT",
                "DTSTART;TZID=Europe/Helsinki:20161223T160000",
                "DTEND;TZID=Europe/Helsinki:20161223T160000",
                "SUMMARY:Test exam 04159adb2253\\, Animal Biotechnology B (KEL/KEBIOT230)",
                "LOCATION:Place: Test 04156f654df1",
                "UID:"),
            eventToString(
                "BEGIN:VEVENT",
                "DTSTART;TZID=Europe/Helsinki:20170131T110000",
                "DTEND;TZID=Europe/Helsinki:20170131T140000",
                "SUMMARY:Formulat... Harjoitus (en)",
                "LOCATION:Arppeanumin auditorio\\, Viikinkaari 11",
                "UID:"),
            eventToString(
                "BEGIN:VEVENT",
                "DTSTART;TZID=Europe/Helsinki:20401028T130000",
                "DTEND;TZID=Europe/Helsinki:20401128T160000",
                "SUMMARY:Tentti\\, Animal Biotechnology B (KEL/KEBIOT230)",
                "LOCATION:Tenttisali",
                "UID:"),
            eventToString(
                "BEGIN:VEVENT",
                "DTSTART;TZID=Europe/Helsinki:20401028T130000",
                "DTEND;TZID=Europe/Helsinki:20401028T235959",
                "SUMMARY:Ei päättymisaikaa\\, Animal Biotechnology B (KEL/KEBIOT230)",
                "LOCATION:Paikka X"
            )
        );

        String expectedFeedEnd = "END:VCALENDAR" + CRLF;

        mockMvc.perform(get(String.format("/api/public/v1/calendar/c9ea7949-577c-458c-a9d9-3c2a39269dd8/%s", language.getCode())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.TEXT_CALENDAR_UTF8))
            .andExpect(content().string(Matchers.allOf(newArrayList(
                contentMatchers(
                    expectedFeedStart,
                    expectedFeedEnd,
                    expectedCalendarEvents)))));
    }

    @Test
    public void thatCalendarFeedIsDisplayedWithOverlappingEventData() throws Exception {
        Language language = Language.EN;

        expectOverlapping(language);

        List<String> expectedCalendarEvents = newArrayList(
            eventToString(
                "BEGIN:VEVENT",
                "DTSTART;TZID=Europe/Helsinki:20161219T141500",
                "DTEND;TZID=Europe/Helsinki:20161219T154500",
                "SUMMARY:Formulat... Harjoitus II (en)",
                "DESCRIPTION:Aku Ankka\\, testauksessa mukana",
                "LOCATION:Päärakennus\\, sali 1\\, Viikinkaari 11\\, Päärakennus\\, "
                    + "sali 2\\, Viikinkaari 11\\, Päärakennus\\, sali 3\\, Viikinkaari 11\\, overlapping where data",
                "UID:")
        );

        mockMvc.perform(get(String.format("/api/public/v1/calendar/c9ea7949-577c-458c-a9d9-3c2a39269dd8/%s", language.getCode())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.TEXT_CALENDAR_UTF8))
            .andExpect(content().string(Matchers.allOf(newArrayList(
                contentMatchers(
                    null,
                    null,
                    expectedCalendarEvents)))));
    }

    private List<Matcher<String>> contentMatchers(String expectedFeedStart, String expectedFeedEnd, List<String> expectedCalendarEvents) {

        List<Matcher<String>> matchers = newArrayList();

        if (expectedFeedStart != null) {
            matchers.add(new StringStartsWith(expectedFeedStart));
        }

        if (expectedFeedEnd != null) {
            matchers.add(new StringEndsWith(expectedFeedEnd));
        }

        matchers.addAll(expectedCalendarEvents.stream()
            .map(StringContains::containsString)
            .collect(Collectors.toList()));

        return matchers;
    }

    @Test
    public void that404IsReturnedWhenUsingIllegalLocale() throws Exception {
        final String lang = "e";
        mockMvc.perform(get(String.format("/api/public/v1/calendar/c9ea7949-577c-458c-a9d9-3c2a39269dd8/%s", lang)))
            .andExpect(status().isNotFound());
    }

    private String eventToString(String... args) {
        return String.join(CRLF, args);
    }

    private void expectEvents(Language language) {
        defaultStudentRequestChain()
            .roles("roleswithstudentrole.json")
            .enrollments()
            .events()
            .defaultImplementationWithLocale(new Locale(language.getCode()));
    }

    private void expectOverlapping(Language language) {
        defaultStudentRequestChain()
            .roles("roleswithstudentrole.json")
            .enrollments()
            .events()
            .courseImplementationWithLocaleRequestChain("123456789", new Locale(language.getCode()), "course_with_overlapping_data.json");
    }

}
