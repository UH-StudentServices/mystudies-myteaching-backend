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

import static fi.helsinki.opintoni.localization.Language.*;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DegreeProgrammeResourceTest extends SpringTest {

    private static final String CODE = "KH50_004";
    private static final String NAME_FI = "Matematiikan, fysiikan ja kemian opettajan kandiohjelma";
    private static final String NAME_SV = "Kandidatprogrammet för ämneslärare i matematik, fysik och kemi";
    private static final String NAME_EN = "Bachelor's Programme for Teachers of Mathematics, Physics and Chemistry";

    private void testDegreeProgrammesWithLocale(Language lang,
                                                String expectedCode,
                                                String expectedName) throws Exception {
        guideServer.expectDegreeProgrammesRequest();

        mockMvc.perform(get("/api/private/v1/degreeprogrammes")
            .with(securityContext(teacherSecurityContext()))
            .cookie(langCookie(lang))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].code").value(expectedCode))
            .andExpect(jsonPath("$[0].name").value(expectedName));
    }

    @Test
    public void thatDegreeProgrammesAreGivenInFinnish() throws Exception {
        testDegreeProgrammesWithLocale(FI, "adkhjaljlskajklsajd", NAME_FI);
    }

    @Test
    public void thatDegreeProgrammesAreGivenInEnglish() throws Exception {
        testDegreeProgrammesWithLocale(EN, CODE, NAME_EN);
    }

    @Test
    public void thatDegreeProgrammesAreGivenInSwedish() throws Exception {
        testDegreeProgrammesWithLocale(SV, CODE, NAME_SV);
    }

}
