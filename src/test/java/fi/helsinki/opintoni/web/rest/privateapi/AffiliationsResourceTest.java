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

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuStudyRegistry;
import fi.helsinki.opintoni.web.WebConstants;

public class AffiliationsResourceTest extends SpringTest {

    @MockBean
    SisuStudyRegistry mockSisuStudyRegistry;

    @Test
    public void getAffiliationsForStudentReturnsCorrectResponse() throws Exception {
        defaultStudentRequestChain().enrollments().studyRights();

        mockMvc.perform(get("/api/private/v1/affiliations").with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.openUniversity").value(false))
            .andExpect(jsonPath("$.faculty.code").value("H70"));
    }

    @Test
    public void getAffiliationsForTeacherReturnsCorrectResponse() throws Exception {
        when(mockSisuStudyRegistry.getTeacherCourses(anyString(), any(LocalDate.class)))
            .thenReturn(courses(List.of("a123", "A124")));

        mockMvc.perform(get("/api/private/v1/affiliations").with(securityContext(teacherSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.openUniversity").value(true))
            .andExpect(jsonPath("$.faculty.code").value("A93000"));
    }

    private List<TeacherCourse> courses(List<String> codes) {
        return codes.stream().map(code -> {
            TeacherCourse c = new TeacherCourse();
            c.learningOpportunityId = code;
            return c;
        }).collect(Collectors.toList());
    }

}
