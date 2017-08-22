package fi.helsinki.opintoni.web.rest.publicapi;

import fi.helsinki.opintoni.SpringTest;
import org.junit.Test;
import static fi.helsinki.opintoni.sampledata.OfficeHoursSampleData.TEACHER_NAME_ARRAY;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



public class PublicOfficeHoursResourceTest extends SpringTest{

    @Test
    public void showProperties() throws Exception {
        mockMvc.perform(get("/api/public/v1/officehours/")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .accept(org.springframework.http.MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*]").exists())
            .andExpect(jsonPath("$.[0].name").exists())
            .andExpect(jsonPath("$.[0].name").value(TEACHER_NAME_ARRAY[0]))
            .andExpect(jsonPath("$.[1].name").value(TEACHER_NAME_ARRAY[1]))
            .andExpect(jsonPath("$.[2].name").value(TEACHER_NAME_ARRAY[2]))
            .andExpect(jsonPath("$.[3].name").value(TEACHER_NAME_ARRAY[3]))
            .andExpect(jsonPath("$.[4].name").value(TEACHER_NAME_ARRAY[4]))
            .andExpect(jsonPath("$.[5].name").value(TEACHER_NAME_ARRAY[5]));

    }

}


