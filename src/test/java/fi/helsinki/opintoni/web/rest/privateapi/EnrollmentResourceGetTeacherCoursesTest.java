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
import fi.helsinki.opintoni.integration.studyregistry.LocalizedText;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistryLocale;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.SisuStudyRegistry;
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EnrollmentResourceGetTeacherCoursesTest extends SpringTest {

    private static final String ROOT_REALISATION_ID = "99903629";
    private static final String OFFICIAL_ROLE_NAME = "official";

    @MockBean
    SisuStudyRegistry mockSisuStudyRegistry;

    private TeacherCourse course(String code) {
        TeacherCourse course = new TeacherCourse();
        course.learningOpportunityId = code;
        course.realisationId = ROOT_REALISATION_ID;
        course.rootId = ROOT_REALISATION_ID;
        course.startDate = LocalDate.of(2015, 10, 26).atStartOfDay();
        course.endDate = LocalDate.of(2016, 4, 27).atStartOfDay();
        course.realisationName = List.of(new LocalizedText(StudyRegistryLocale.FI, "Formulointi III"));
        course.teacherRole = OFFICIAL_ROLE_NAME;
        return course;
    }

    @Test
    public void thatTeacherCoursesAreReturned() throws Exception {
        defaultTeacherRequestChain().courseImplementationWithRealisationId(ROOT_REALISATION_ID).and();
        when(mockSisuStudyRegistry.getTeacherCourses(anyString(), any(LocalDate.class))).thenReturn(List.of(course("10440")));

        mockMvc.perform(get("/api/private/v1/teachers/enrollments/courses")
            .with(securityContext(teacherSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].code").value("10440"))
            .andExpect(jsonPath("$[0].name").value("Formulointi III"))
            .andExpect(jsonPath("$[0].startDate[0]").value(2015))
            .andExpect(jsonPath("$[0].startDate[1]").value(10))
            .andExpect(jsonPath("$[0].startDate[2]").value(26))
            .andExpect(jsonPath("$[0].startDate[3]").value(0))
            .andExpect(jsonPath("$[0].startDate[4]").value(0))
            .andExpect(jsonPath("$[0].endDate[0]").value(2016))
            .andExpect(jsonPath("$[0].endDate[1]").value(4))
            .andExpect(jsonPath("$[0].endDate[2]").value(27))
            .andExpect(jsonPath("$[0].endDate[3]").value(0))
            .andExpect(jsonPath("$[0].endDate[4]").value(0))
            .andExpect(jsonPath("$[0].isExam").value(false))
            .andExpect(jsonPath("$[0].isCancelled").value(false))
            .andExpect(jsonPath("$[0].realisationId").value(ROOT_REALISATION_ID))
            .andExpect(jsonPath("$[0].parentId").isEmpty())
            .andExpect(jsonPath("$[0].teacherRole").value(OFFICIAL_ROLE_NAME));
    }

}
