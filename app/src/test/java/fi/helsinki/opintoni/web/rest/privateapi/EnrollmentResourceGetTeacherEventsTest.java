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
import fi.helsinki.opintoni.web.TestConstants;
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EnrollmentResourceGetTeacherEventsTest extends SpringTest {

    @Test
    public void thatTeacherEventsAreReturned() throws Exception {
        expectEvents();

        mockMvc.perform(get("/api/private/v1/teachers/enrollments/events")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(4)))
            .andExpect(jsonPath("$[0].realisationId").value(99903629))
            .andExpect(jsonPath("$[0].endDate").isArray())
            .andExpect(jsonPath("$[0].endDate[0]").value(2015))
            .andExpect(jsonPath("$[0].endDate[1]").value(8))
            .andExpect(jsonPath("$[0].endDate[2]").value(13))
            .andExpect(jsonPath("$[0].endDate[3]").value(14))
            .andExpect(jsonPath("$[0].endDate[4]").value(0))
            .andExpect(jsonPath("$[0].startDate").isArray())
            .andExpect(jsonPath("$[0].startDate[0]").value(2015))
            .andExpect(jsonPath("$[0].startDate[1]").value(8))
            .andExpect(jsonPath("$[0].startDate[2]").value(13))
            .andExpect(jsonPath("$[0].startDate[3]").value(10))
            .andExpect(jsonPath("$[0].startDate[4]").value(0))
            .andExpect(jsonPath("$[0].title").value("Formulation III"))
            .andExpect(jsonPath("$[0].type").value(EventDto.Type.DEFAULT.name()))
            .andExpect(jsonPath("$[0].source").value(EventDto.Source.OODI.name()))
            .andExpect(jsonPath("$[0].courseTitle").value("Animal Biotechnology B (KEL/KEBIOT230)"))
            .andExpect(jsonPath("$[0].courseMaterial.courseMaterialUri")
                .value("https://dev.student.helsinki.fi/tvt?group-imp-material"))
            .andExpect(jsonPath("$[0].courseMaterial.courseMaterialType")
                .value(CourseMaterialDto.CourseMaterialType.COURSE_PAGE.toString()))
            .andExpect(jsonPath("$[0].locations[0].locationString").value("F24 Fabianinkatu 24, Sali 531, Viikinkaari 11"))
            .andExpect(jsonPath("$[0].locations[0].roomName").value("F24 Fabianinkatu 24, Sali 531"))
            .andExpect(jsonPath("$[0].locations[0].streetAddress").value("Viikinkaari 11"))
            .andExpect(jsonPath("$[0].locations[0].zipCode").value("00790"))
            .andExpect(jsonPath("$[0].hasMaterial").value(true))
            .andExpect(jsonPath("$[3].title").value("Tentti"))
            .andExpect(jsonPath("$[3].source").value(EventDto.Source.COURSE_PAGE.name()))
            .andExpect(jsonPath("$[3].type").value(EventDto.Type.EXAM.name()));
    }

    private void expectEvents() {
        defaultTeacherRequestChain()
            .events()
            .defaultCourseImplementation()
            .and()
            .courses("teachercourses_singlecourse.json")
            .courseUnitRealisation(TestConstants.TEACHER_COURSE_REALISATION_ID);
    }

}
