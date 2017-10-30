package fi.helsinki.opintoni.web.rest.publicapi;

import fi.helsinki.opintoni.SpringTest;
import org.junit.Test;

import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.DEGREE_CODE_1;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.DEGREE_CODE_2;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.DEGREE_CODE_3;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.OFFICE_HOURS;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.OFFICE_HOURS_7;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.TEACHER_NAME_ARRAY;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static fi.helsinki.opintoni.service.OfficeHoursService.OFFICE_HOURS_JOIN;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



public class PublicOfficeHoursResourceTest extends SpringTest{

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

}


