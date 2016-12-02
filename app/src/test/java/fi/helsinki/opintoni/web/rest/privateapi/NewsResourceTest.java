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
import fi.helsinki.opintoni.web.WebConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Locale;

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

        mockMvc.perform(get("/api/private/v1/news/student")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .locale(new Locale("fi"))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].title").value("Ajatusten vaihtoa ja vertaistukea verkossa"))
            .andExpect(jsonPath("$[0].url").value("https://flamma.helsinki.fi/portal/home/sisalto1"))
            .andExpect(jsonPath("$[0].content").value("Content"))
            .andExpect(jsonPath("$[1].title").value("Reflekta palkittiin parhaana opiskelijakilpailussa"))
            .andExpect(jsonPath("$[1].url").value("https://flamma.helsinki.fi/portal/home/sisalto2"))
            .andExpect(jsonPath("$[1].content").value("Content"));
    }

    @Test
    public void thatTeacherNewsAreReturned() throws Exception {
        flammaServer.expectTeacherNews();

        mockMvc.perform(get("/api/private/v1/news/teacher")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .locale(new Locale("fi"))
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

        mockMvc.perform(get("/api/private/v1/news/student")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .locale(new Locale("en"))
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
    public void thatEnglishTeacherNewsAreReturned() throws Exception {
        flammaServer.expectEnglishTeacherNews();

        mockMvc.perform(get("/api/private/v1/news/teacher")
            .with(securityContext(teacherSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .locale(new Locale("en"))
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

        mockMvc.perform(get("/api/private/v1/news/student/openuniversity")
            .with(securityContext(studentSecurityContext()))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .locale(new Locale("fi"))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title").value("Otsikko"))
            .andExpect(jsonPath("$[0].url").value("https://www.helsinki.fi/fi/uutiset/otsikko"))
            .andExpect(jsonPath("$[0].content").value("Sisältö"));
    }

}
