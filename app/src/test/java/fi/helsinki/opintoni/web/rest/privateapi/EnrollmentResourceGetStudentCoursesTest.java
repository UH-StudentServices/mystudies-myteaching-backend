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
import fi.helsinki.opintoni.web.TestConstants;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.requestchain.StudentRequestChain;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.dto.portfolio.CourseMaterialDto.CourseMaterialType;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EnrollmentResourceGetStudentCoursesTest extends SpringTest {

    private static final String COURSE_PAGE_COURSE_MATERIAL_URL = "https://dev.student.helsinki.fi/tvt?group-imp-material";
    private static final String MOODLE_COURSE_MATERIAL_URL = "http://moodle.helsinki.fi";
    private static final String WIKI_COURSE_MATERIAL_URL = "http://wiki.helsinki.fi";

    @Test
    public void thatStudentCoursesAreReturned() throws Exception {
        expectCourseRequestChain()
            .defaultCourseUnitRealisation();

        thatStudentCoursesAreReturned(false, COURSE_PAGE_COURSE_MATERIAL_URL, CourseMaterialType.COURSE_PAGE);
    }

    @Test
    public void thatStudentCoursesAreReturnedWithMoodleMaterial() throws Exception {
        expectCourseRequestChainWithImplementation("courses_with_moodle_url.json");

        thatStudentCoursesAreReturned(false, MOODLE_COURSE_MATERIAL_URL, CourseMaterialType.MOODLE);
    }

    @Test
    public void thatStudentCoursesAreReturnedWitWikiMaterial() throws Exception {
        expectCourseRequestChainWithImplementation("courses_with_wiki_url.json");

        thatStudentCoursesAreReturned(false, WIKI_COURSE_MATERIAL_URL, CourseMaterialType.WIKI);
    }

    @Test
    public void thatCancelledCourseStatusIsReturned() throws Exception{
        expectCourseRequestChain()
            .cancelledCourseUnitRealisation();

        thatStudentCoursesAreReturned(true, COURSE_PAGE_COURSE_MATERIAL_URL, CourseMaterialType.COURSE_PAGE);
    }

    private void expectCourseRequestChainWithImplementation(String responseFile) {
        defaultStudentRequestChain()
            .enrollments()
            .courseImplementation(TestConstants.STUDENT_COURSE_REALISATION_ID, responseFile)
            .and()
            .defaultCourseUnitRealisation();
    }

    private StudentRequestChain expectCourseRequestChain() {
        return defaultStudentRequestChain()
            .enrollments()
            .defaultImplementation()
            .and();
    }

    private void thatStudentCoursesAreReturned(boolean expectedCancellation,
                                               String expectedCourseMaterialUri,
                                               CourseMaterialType expectedCourseMaterialType) throws Exception {
        mockMvc.perform(get("/api/private/v1/students/enrollments/courses")
            .with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].code").value("52736"))
            .andExpect(jsonPath("$[0].name").value("Gene Technology"))
            .andExpect(jsonPath("$[0].startDate").isArray())
            .andExpect(jsonPath("$[0].startDate[0]").value(2016))
            .andExpect(jsonPath("$[0].startDate[1]").value(1))
            .andExpect(jsonPath("$[0].startDate[2]").value(4))
            .andExpect(jsonPath("$[0].startDate[3]").value(22))
            .andExpect(jsonPath("$[0].startDate[4]").value(0))
            .andExpect(jsonPath("$[0].credits").value(3))
            .andExpect(jsonPath("$[0].endDate").isArray())
            .andExpect(jsonPath("$[0].endDate[0]").value(2016))
            .andExpect(jsonPath("$[0].endDate[1]").value(2))
            .andExpect(jsonPath("$[0].endDate[2]").value(3))
            .andExpect(jsonPath("$[0].endDate[3]").value(22))
            .andExpect(jsonPath("$[0].endDate[4]").value(0))
            .andExpect(jsonPath("$[0].webOodiUri").value("https://weboodi.helsinki.fi"))
            .andExpect(jsonPath("$[0].courseMaterial.courseMaterialUri").value(expectedCourseMaterialUri))
            .andExpect(jsonPath("$[0].courseMaterial.courseMaterialType").value(expectedCourseMaterialType.toString()))
            .andExpect(jsonPath("$[0].teachers[0]").value("Rantala Kari A"))
            .andExpect(jsonPath("$[0].isCancelled").value(expectedCancellation))
            .andExpect(jsonPath("$[0].parentId").isEmpty());
    }

}
