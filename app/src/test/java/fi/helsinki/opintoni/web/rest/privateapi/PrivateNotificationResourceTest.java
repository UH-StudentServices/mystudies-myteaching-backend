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
import fi.helsinki.opintoni.config.Constants;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import javax.servlet.http.Cookie;
import java.util.Locale;

import static fi.helsinki.opintoni.localization.Language.EN;
import static fi.helsinki.opintoni.localization.Language.FI;
import static fi.helsinki.opintoni.localization.Language.SV;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateNotificationResourceTest extends SpringTest {

    private static final String NOTIFICATION_TEXT_FI = "Huoltokatkoilmoitus 1 (fi)";
    private static final String NOTIFICATION_TEXT_EN = "Huoltokatkoilmoitus 1 (en)";
    private static final String NOTIFICATION_TEXT_SV = "Huoltokatkoilmoitus 1 (sv)";

    private static final String NOTIFICATION_RESOURCE_PATH = RestConstants.PRIVATE_API_V1 + "/notifications";

    private void assertNotificationsForLocale(Language lang, String expectedNotificationText) throws Exception {
        mockMvc.perform(get(NOTIFICATION_RESOURCE_PATH)
            .with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON)
            .cookie(langCookie(lang)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].text").value(expectedNotificationText));
    }

    @Test
    public void thatActiveNotificationIsReturnedInEnglish() throws Exception {
       assertNotificationsForLocale(EN, NOTIFICATION_TEXT_EN);
    }

    @Test
    public void thatActiveNotificationIsReturnedInFinnish() throws Exception {
        assertNotificationsForLocale(FI, NOTIFICATION_TEXT_FI);
    }

    @Test
    public void thatActiveNotificationIsReturnedInSwedish() throws Exception {
        assertNotificationsForLocale(SV, NOTIFICATION_TEXT_SV);
    }

}
