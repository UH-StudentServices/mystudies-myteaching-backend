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

package fi.helsinki.opintoni.web.rest.publicapi.profile;

import fi.helsinki.opintoni.SpringTest;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import static com.google.common.base.Charsets.UTF_8;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicFilesResourceTest extends SpringTest {

    private static final String PRIVATE_FILES_RESOURCE_PATH = "/api/private/v1/profile/files";
    private static final String TEST_FILE_NAME = "test.txt";
    private static final String TEST_FILE_CONTENT = "test";

    @Test
    public void thatPublicProfileFilesAreReturnedForUnauthenticatedUser() throws Exception {
        String url = postAndGetFileUrl();

        mockMvc.perform(get(url))
            .andExpect(status().isOk())
            .andExpect(content().string(TEST_FILE_CONTENT));
    }

    private String postAndGetFileUrl() throws Exception {
        ResultActions result = performPostFile();
        String content = result.andReturn().getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(content);
        return jsonObject.get("url").toString();
    }

    private ResultActions performPostFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("upload", TEST_FILE_NAME, "text/plain", TEST_FILE_CONTENT.getBytes(UTF_8));
        return mockMvc.perform(fileUpload(PRIVATE_FILES_RESOURCE_PATH).file(file)
            .with(securityContext(studentSecurityContext())));
    }
}
