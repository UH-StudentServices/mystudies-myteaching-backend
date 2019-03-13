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

package fi.helsinki.opintoni.web.rest.privateapi.profile;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.sampledata.SampleDataFiles;
import fi.helsinki.opintoni.service.UserSettingsService;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.ResultActions;

import static fi.helsinki.opintoni.localization.Language.EN;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public abstract class AbstractProfileResourceTest extends SpringTest {

    protected static final String PRIVATE_PROFILE_API_PATH = RestConstants.PRIVATE_API_V1 + "/profile";
    protected static final String STUDENT_PROFILE_API_PATH = PRIVATE_PROFILE_API_PATH + "/student";
    protected static final String TEACHER_PROFILE_API_PATH = PRIVATE_PROFILE_API_PATH + "/teacher";
    protected static final String SESSION_LANG = EN.getCode();
    protected static final String ABSOLUTE_BASE_URL = "https://dev.student.helsinki.fi";
    protected static final String ABSOLUTE_PUBLIC_API_PATH = ABSOLUTE_BASE_URL + RestConstants.PUBLIC_API_V1;
    protected static final String ABSOLUTE_RESTRICTED_API_PATH = ABSOLUTE_BASE_URL + RestConstants.RESTRICTED_API_V1;
    protected static final String PROFILE_IMAGE = "/profile-image";

    private static final String EMPLOYEE_NUMBER = "010540";

    @Autowired
    private UserSettingsService userSettingsService;

    @Before
    public final void addAvatars() {
        userSettingsService.updateUserAvatar(3L, getAvatarImageData()); // olli-opiskelija
        userSettingsService.updateUserAvatar(4L, getAvatarImageData()); // opettaja
    }

    protected ResultActions createProfile(SecurityContext securityContext, String apiUrl) throws Exception {
        return mockMvc.perform(post(apiUrl)
            .cookie(langCookie(EN))
            .with(securityContext(securityContext))
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions createStudentProfile(SecurityContext securityContext) throws Exception {
        return createProfile(securityContext, STUDENT_PROFILE_API_PATH);
    }

    protected ResultActions createStudentProfile(SecurityContext securityContext, Language lang) throws Exception {
        return createProfile(securityContext, String.join("/", STUDENT_PROFILE_API_PATH, lang.getCode()));
    }

    protected ResultActions createTeacherProfile(SecurityContext securityContext) throws Exception {
        return createProfile(securityContext, TEACHER_PROFILE_API_PATH);
    }

    protected ResultActions createTeacherProfile(SecurityContext securityContext, Language lang) throws Exception {
        return createProfile(securityContext, String.join("/", TEACHER_PROFILE_API_PATH, lang.getCode()));
    }

    protected void expectEmployeeContactInformationRequestToESB() {
        esbServer.expectEmployeeContactInformationRequest(EMPLOYEE_NUMBER);
    }

    protected String getAvatarImageData() {
        return SampleDataFiles.imageToBase64("usersettings/useravatar.jpg");
    }
}
