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
import org.junit.Test;

import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.ADDITIONAL_INFO;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.ADDITIONAL_INFO_2;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.DEGREE_CODE_1;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.DEGREE_CODE_2;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.DEGREE_CODE_3;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.OFFICE_HOURS;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.OFFICE_HOURS_7;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.LOCATION;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.LOCATION_2;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.TEACHER_NAME_ARRAY;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static fi.helsinki.opintoni.service.OfficeHoursService.OFFICE_HOURS_JOIN;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class PublicOfficeHoursResourceTest extends SpringTest {

    @Test
    public void showProperties() throws Exception {
        mockMvc.perform(get("/api/public/v1/officehours/")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(org.springframework.http.MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(6)))
            .andExpect(jsonPath("$.[0].name").value(TEACHER_NAME_ARRAY[0]))
            .andExpect(jsonPath("$.[1].name").value(TEACHER_NAME_ARRAY[1]))
            .andExpect(jsonPath("$.[2].name").value(TEACHER_NAME_ARRAY[2]))
            .andExpect(jsonPath("$.[3].name").value(TEACHER_NAME_ARRAY[3]))
            .andExpect(jsonPath("$.[4].name").value(TEACHER_NAME_ARRAY[4]))
            .andExpect(jsonPath("$.[5].name").value(TEACHER_NAME_ARRAY[5]))
            .andExpect(jsonPath("$.[4].officeHours").value(
                OFFICE_HOURS + OFFICE_HOURS_JOIN + OFFICE_HOURS_7
            ))
            .andExpect(jsonPath("$.[4].degreeProgrammes", hasSize(3)))
            .andExpect(jsonPath("$.[4].degreeProgrammes", containsInAnyOrder(DEGREE_CODE_1, DEGREE_CODE_2, DEGREE_CODE_3)));
    }

    @Test
    public void getOfficeHoursV2() throws Exception {
        mockMvc.perform(get("/api/public/v2/officehours/")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(org.springframework.http.MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(6)))
            .andExpect(jsonPath("$.[0].name").value(TEACHER_NAME_ARRAY[0]))
            .andExpect(jsonPath("$.[1].name").value(TEACHER_NAME_ARRAY[1]))
            .andExpect(jsonPath("$.[2].name").value(TEACHER_NAME_ARRAY[2]))
            .andExpect(jsonPath("$.[3].name").value(TEACHER_NAME_ARRAY[3]))
            .andExpect(jsonPath("$.[4].name").value(TEACHER_NAME_ARRAY[4]))
            .andExpect(jsonPath("$.[5].name").value(TEACHER_NAME_ARRAY[5]))
            .andExpect(jsonPath("$.[4].officeHours", hasSize(2)))
            .andExpect(jsonPath("$.[4].officeHours.[0].description").value(OFFICE_HOURS))
            .andExpect(jsonPath("$.[4].officeHours.[0].additionalInfo").value(ADDITIONAL_INFO))
            .andExpect(jsonPath("$.[4].officeHours.[0].location").value(LOCATION))
            .andExpect(jsonPath("$.[4].officeHours.[1].description").value(OFFICE_HOURS_7))
            .andExpect(jsonPath("$.[4].officeHours.[1].additionalInfo").value(ADDITIONAL_INFO_2))
            .andExpect(jsonPath("$.[4].officeHours.[1].location").value(LOCATION_2))
            .andExpect(jsonPath("$.[4].officeHours.[0].degreeProgrammes", containsInAnyOrder(DEGREE_CODE_1, DEGREE_CODE_2)))
            .andExpect(jsonPath("$.[4].officeHours.[1].degreeProgrammes", containsInAnyOrder(DEGREE_CODE_2, DEGREE_CODE_3)));
    }

}


