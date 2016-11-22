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
import fi.helsinki.opintoni.localization.Language;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Locale;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RecommendationResourceTest extends SpringTest {

    private final static String EXPECTED_COURSE_TITLE_FI = "Henkilökohtainen opintosuunnitelma (HOPS) (ENG321)";
    private final static String EXPECTED_COURSE_TITLE_EN = "Henkilökohtainen opintosuunnitelma (HOPS) (ENG321) (en)";
    private final static String EXPECTED_COURSE_TITLE_SV = "Henkilökohtainen opintosuunnitelma (HOPS) (ENG321) (sv)";

    private final static String EXPECTED_COURSE_LINK_FI = "https://dev.student.helsinki.fi/fi/590155/107694052";
    private final static String EXPECTED_COURSE_LINK_EN = "https://dev.student.helsinki.fi/590155/107694052";
    private final static String EXPECTED_COURSE_LINK_SV = "https://dev.student.helsinki.fi/sv/590155/107694052";

    private final static String STUDENT_NUMBER = "010189791";

    private void testCourseRecommendationsWithLocale(String languageCode, String expectedTitle, String expectedLink) throws Exception{
        leikiServer.expectCourseRecommendationsResult(STUDENT_NUMBER, "courserecommendationresults.json");

        mockMvc.perform(get("/api/private/v1/recommendations/courses").with(securityContext(studentSecurityContext()))
            .locale(new Locale(languageCode))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(20)))
            .andExpect(jsonPath("$[0].title").value(expectedTitle))
            .andExpect(jsonPath("$[0].link").value(expectedLink));
    }

    @Test
    public void thatCourseRecommendationsAreReturnedInFinnish() throws Exception {
        testCourseRecommendationsWithLocale(
            Language.FI.getCode(),
            EXPECTED_COURSE_TITLE_FI,
            EXPECTED_COURSE_LINK_FI);
    }

    @Test
    public void thatCourseRecommendationsAreReturnedInEnglish() throws Exception {
        testCourseRecommendationsWithLocale(
            Language.EN.getCode(),
            EXPECTED_COURSE_TITLE_EN,
            EXPECTED_COURSE_LINK_EN);
    }

    @Test
    public void thatCourseRecommendationsAreReturnedInSwedish() throws Exception {
        testCourseRecommendationsWithLocale(
            Language.SV.getCode(),
            EXPECTED_COURSE_TITLE_SV,
            EXPECTED_COURSE_LINK_SV);
    }

    @Test
    public void thatEmptyCourseRecommendationsAreReturnedWhenLeikiReturnsErrorResponse() throws Exception {
        leikiServer.expectCourseRecommendationsErrorResult("010189791");

        mockMvc.perform(get("/api/private/v1/recommendations/courses").with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(0)));
    }
}
