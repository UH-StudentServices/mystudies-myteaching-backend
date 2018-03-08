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
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.localization.Language.*;
import static fi.helsinki.opintoni.localization.Language.FI;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NewsResourceTest extends SpringTest {

    @Test
    public void thatStudentNewsAreReturned() throws Exception {
        flammaServer.expectStudentNews();
        guideNewsServer.expectGuideNewsFi();
        oodiServer.expectStudentStudyRightsRequest(TestConstants.STUDENT_NUMBER);

        mockMvc.perform(get("/api/private/v1/news/student")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(langCookie(FI))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(4)))
            .andExpect(jsonPath("$[0].title").value("Flammatitle1"))
            .andExpect(jsonPath("$[0].url").value("https://flamma.helsinki.fi/fi/flammatitle1"))
            .andExpect(jsonPath("$[0].content").value("Flammacontent1"))
            .andExpect(jsonPath("$[1].title").value("Guidetitle1"))
            .andExpect(jsonPath("$[1].url").value("https://guide.student.helsinki.fi/fi/guidetitle1"))
            .andExpect(jsonPath("$[1].content").value("Guidesummary1"));
    }

    @Test
    public void thatTeacherNewsAreReturned() throws Exception {
        flammaServer.expectTeacherNews();

        mockMvc.perform(get("/api/private/v1/news/teacher")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(langCookie(FI))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].title").value("Reflekta palkittiin parhaana opiskelijakilpailussa"))
            .andExpect(jsonPath("$[0].url").value("https://flamma.helsinki.fi/portal/home/sisalto1"))
            .andExpect(jsonPath("$[0].content").value("Content"))
            .andExpect(jsonPath("$[1].title").value("Tukea yliopisto-opettajille"))
            .andExpect(jsonPath("$[1].url").value("https://flamma.helsinki.fi/portal/home/sisalto2"))
            .andExpect(jsonPath("$[1].content").value("Content"));
    }

    @Test
    public void thatEnglishStudentNewsAreReturned() throws Exception {
        flammaServer.expectEnglishStudentNews();
        guideNewsServer.expectGuideNewsEn();
        oodiServer.expectStudentStudyRightsRequest(TestConstants.STUDENT_NUMBER);

        mockMvc.perform(get("/api/private/v1/news/student")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(langCookie(EN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(4)))
            .andExpect(jsonPath("$[0].title").value("Flammatitle1-en"))
            .andExpect(jsonPath("$[0].url").value("https://flamma.helsinki.fi/fi/flammatitle1-en"))
            .andExpect(jsonPath("$[0].content").value("Flammacontent1-en"))
            .andExpect(jsonPath("$[1].title").value("Guidetitle1-en"))
            .andExpect(jsonPath("$[1].url").value("https://guide.student.helsinki.fi/fi/guidetitle1-en"))
            .andExpect(jsonPath("$[1].content").value("Guidesummary1-en"));
    }

    @Test
    public void thatEnglishTeacherNewsAreReturned() throws Exception {
        flammaServer.expectEnglishTeacherNews();

        mockMvc.perform(get("/api/private/v1/news/teacher")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(langCookie(EN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title").value("Title"))
            .andExpect(jsonPath("$[0].url").value("https://flamma.helsinki.fi/portal/home/content"))
            .andExpect(jsonPath("$[0].content").value("Content"));
    }

    @Test
    public void thatStudentOpenUniversityNewsAreReturned() throws Exception {
        publicWwwServer.expectStudentOpenUniversityNews();

        mockMvc.perform(get("/api/private/v1/news/openuniversity")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(langCookie(FI))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title").value("Otsikko"))
            .andExpect(jsonPath("$[0].url").value("https://www.helsinki.fi/fi/uutiset/otsikko"))
            .andExpect(jsonPath("$[0].content").value("Sisältö"));
    }

    @Test
    public void thatStudentNewsIncludingProgrammeNewsAreReturned() throws Exception {
        flammaServer.expectStudentNews();
        oodiServer.expectStudentStudyRightsRequest(TestConstants.STUDENT_NUMBER, "studentstudyrights_with_KH_and_MH_codes.json");
        guideNewsServer.expectGuideProgrammeNewsFi("MH80_xxx", "feed-MH80_xxx.xml");
        guideNewsServer.expectGuideProgrammeNewsFi("KH57_001", "feed.xml");

        mockMvc.perform(get("/api/private/v1/news/student")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(langCookie(FI))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(4)))

            .andExpect(jsonPath("$[0].title").value("Flammatitle1"))
            .andExpect(jsonPath("$[0].url").value("https://flamma.helsinki.fi/fi/flammatitle1"))
            .andExpect(jsonPath("$[0].content").value("Flammacontent1"))

            .andExpect(jsonPath("$[1].title").value("Guidetitle1"))
            .andExpect(jsonPath("$[1].url").value("https://guide.student.helsinki.fi/fi/guidetitle1"))
            .andExpect(jsonPath("$[1].content").value("Guidesummary1"))

            .andExpect(jsonPath("$[2].title").value("Guidetitle1_mh80"))
            .andExpect(jsonPath("$[2].url").value("https://guide.student.helsinki.fi/fi/guidetitle1mh80"))
            .andExpect(jsonPath("$[2].content").value("Guidesummary_mh80"));
    }

    @Test
    public void thatMultipleCallsReturnSameNews() throws Exception {

        // Stems from  OO-966 in which some news items were duplicated on consecutive api calls.
        // This test checks against the fix.

        flammaServer.expectStudentNews();
        guideNewsServer.expectGuideNewsFi();
        oodiServer.expectStudentStudyRightsRequest(TestConstants.STUDENT_NUMBER);

        performGetNewsForStudentsWithChecks();
        performGetNewsForStudentsWithChecks();
    }

    @Test
    public void thatFlammaNewsAreReturnedIfOodiIsDown() throws Exception {
        flammaServer.expectStudentNews();
        oodiServer.expectStudentStudyRightsRequestToRespondError(TestConstants.STUDENT_NUMBER);
        guideNewsServer.expectGuideNewsFi();

        mockMvc.perform(get("/api/private/v1/news/student")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(langCookie(FI))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(4)))

            .andExpect(jsonPath("$[0].title").value("Flammatitle1"))
            .andExpect(jsonPath("$[0].url").value("https://flamma.helsinki.fi/fi/flammatitle1"))
            .andExpect(jsonPath("$[0].content").value("Flammacontent1"))

            .andExpect(jsonPath("$[1].title").value("Guidetitle1"))
            .andExpect(jsonPath("$[1].url").value("https://guide.student.helsinki.fi/fi/guidetitle1"))
            .andExpect(jsonPath("$[1].content").value("Guidesummary1"));
    }

    private void performGetNewsForStudentsWithChecks() throws Exception {
        mockMvc.perform(get("/api/private/v1/news/student")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(langCookie(FI))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(4)))
            .andExpect(jsonPath("$[0].title").value("Flammatitle1"))
            .andExpect(jsonPath("$[1].title").value("Guidetitle1"))
            .andExpect(jsonPath("$[2].title").value("Flammatitle2"))
            .andExpect(jsonPath("$[3].title").value("Guidetitle2"));
    }

}
