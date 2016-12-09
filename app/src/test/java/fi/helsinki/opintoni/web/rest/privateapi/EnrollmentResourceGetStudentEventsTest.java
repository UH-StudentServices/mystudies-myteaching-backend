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

package fi.helsinki.opintoni.web.rest.privateapi;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.EventDto;
import fi.helsinki.opintoni.dto.portfolio.CourseMaterialDto;
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EnrollmentResourceGetStudentEventsTest extends SpringTest {

    @Test
    public void thatStudentEventsAreReturned() throws Exception {
        expectEvents();

        mockMvc.perform(get("/api/private/v1/students/enrollments/events")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(7)))
            .andExpect(jsonPath("$[0].realisationId").value(123456789))
            .andExpect(jsonPath("$[0].endDate").isArray())
            .andExpect(jsonPath("$[0].endDate[0]").value(2016))
            .andExpect(jsonPath("$[0].endDate[1]").value(12))
            .andExpect(jsonPath("$[0].endDate[2]").value(9))
            .andExpect(jsonPath("$[0].endDate[3]").value(15))
            .andExpect(jsonPath("$[0].endDate[4]").value(45))
            .andExpect(jsonPath("$[0].startDate").isArray())
            .andExpect(jsonPath("$[0].startDate[0]").value(2016))
            .andExpect(jsonPath("$[0].startDate[1]").value(12))
            .andExpect(jsonPath("$[0].startDate[2]").value(9))
            .andExpect(jsonPath("$[0].startDate[3]").value(14))
            .andExpect(jsonPath("$[0].startDate[4]").value(15))
            .andExpect(jsonPath("$[0].title").value("Formulat... Harjoitus II (en)"))
            .andExpect(jsonPath("$[0].type").value(EventDto.Type.DEFAULT.name()))
            .andExpect(jsonPath("$[0].source").value(EventDto.Source.OODI.name()))
            .andExpect(jsonPath("$[0].courseMaterial.courseMaterialUri")
                .value("https://dev.student.helsinki.fi/tvt?group-imp-material"))
            .andExpect(jsonPath("$[0].courseMaterial.courseMaterialType")
                .value(CourseMaterialDto.CourseMaterialType.COURSE_PAGE.toString()))
            .andExpect(jsonPath("$[0].location.locationString").value("Päärakennus, sali 1, Viikinkaari 11"))
            .andExpect(jsonPath("$[0].location.roomName").value("Päärakennus, sali 1"))
            .andExpect(jsonPath("$[0].location.streetAddress").value("Viikinkaari 11"))
            .andExpect(jsonPath("$[0].location.zipCode").value("00790"))
            .andExpect(jsonPath("$[0].hasMaterial").value(true))
            .andExpect(jsonPath("$[3].title").value("Test exam 04159adb2253"))
            .andExpect(jsonPath("$[3].source").value(EventDto.Source.COURSE_PAGE.name()))
            .andExpect(jsonPath("$[3].type").value(EventDto.Type.EXAM.name()));
    }

    private void expectEvents() {
        defaultStudentRequestChain()
            .events()
            .defaultImplementation()
            .and()
            .enrollments()
            .defaultCourseUnitRealisation()
            .defaultOneOffEvents();
    }

}

