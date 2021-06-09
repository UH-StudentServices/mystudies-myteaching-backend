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
import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuStudyRegistry;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This class only used to test the calendar feed for students.
 * Now only calendar feed for teachers is implemented, but instead of that, Optime calendar feed should be used.
 * The system is sunsetting, so we will not use effort for writing tests for teacher calendar feed at this time.
 */
public class PublicCalendarFeedResourceTest extends SpringTest {

    @MockBean
    SisuStudyRegistry mockSisuStudyRegistry;

    @Test
    public void that404IsReturnedWhenUsingIllegalLocale() throws Exception {
        final String lang = "e";
        mockMvc.perform(get(String.format("/api/public/v1/calendar/c9ea7949-577c-458c-a9d9-3c2a39269dd8/%s", lang)))
            .andExpect(status().isNotFound());
    }
}
