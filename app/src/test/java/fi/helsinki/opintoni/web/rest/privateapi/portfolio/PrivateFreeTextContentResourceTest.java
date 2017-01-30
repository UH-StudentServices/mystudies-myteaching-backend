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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.portfolio.TeacherPortfolioSection;
import fi.helsinki.opintoni.dto.portfolio.FreeTextContentDto;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateFreeTextContentResourceTest extends SpringTest {

    private static final String STUDENT_API_PATH = "/portfolio/2/freetextcontent";
    private static final String TEACHER_API_PATH = "/portfolio/4/freetextcontent";
    private static final String API_PATH_FREETEXTCONTENT_ID = "/1";
    private static final String NEW_TITLE = "Uusi otsikko";
    private static final String NEW_TEXT = "Uusi teksti";
    private static final String INSTANCE_NAME = "Testi-ID";

    @Test
    public void thatFreeTextContentIsInserted() throws Exception {
        FreeTextContentDto freeTextContentDto = new FreeTextContentDto();
        freeTextContentDto.title = NEW_TITLE;
        freeTextContentDto.text = NEW_TEXT;

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH)
            .with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(freeTextContentDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(NEW_TITLE))
            .andExpect(jsonPath("$.text").value(NEW_TEXT));
    }

    @Test
    public void thatSectionBoundFreeTextContentIsInserted() throws Exception {
        FreeTextContentDto freeTextContentDto = new FreeTextContentDto();
        freeTextContentDto.title = NEW_TITLE;
        freeTextContentDto.text = NEW_TEXT;
        freeTextContentDto.portfolioSection = TeacherPortfolioSection.BASIC_INFORMATION.toString();
        freeTextContentDto.instanceName = INSTANCE_NAME;

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + TEACHER_API_PATH)
            .with(securityContext(teacherSecurityContext()))
            .content(WebTestUtils.toJsonBytes(freeTextContentDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(NEW_TITLE))
            .andExpect(jsonPath("$.text").value(NEW_TEXT))
            .andExpect(jsonPath("$.portfolioSection").value(TeacherPortfolioSection.BASIC_INFORMATION.toString()))
            .andExpect(jsonPath("$.instanceName").value(INSTANCE_NAME));
    }

   @Test
   public void thatFreeTextContentIsUpdated() throws Exception {
       FreeTextContentDto freeTextContentDto = new FreeTextContentDto();
       freeTextContentDto.title = NEW_TITLE;
       freeTextContentDto.text = NEW_TEXT;

       mockMvc.perform(put(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH + API_PATH_FREETEXTCONTENT_ID)
           .with(securityContext(studentSecurityContext()))
           .content(WebTestUtils.toJsonBytes(freeTextContentDto))
           .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.title").value(NEW_TITLE))
           .andExpect(jsonPath("$.text").value(NEW_TEXT));
   }

    @Test
    public void thatFreeTextContentIsDeleted() throws Exception {
        mockMvc.perform(delete(RestConstants.PRIVATE_API_V1 + STUDENT_API_PATH + API_PATH_FREETEXTCONTENT_ID)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isNoContent());
    }
}
