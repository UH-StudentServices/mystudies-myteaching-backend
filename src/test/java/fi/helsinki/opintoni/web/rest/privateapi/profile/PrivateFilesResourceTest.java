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
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static com.google.common.base.Charsets.UTF_8;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateFilesResourceTest extends SpringTest {

    private static final String PRIVATE_FILES_RESOURCE_PATH = "/api/private/v1/profile/files";
    private static final String PUBLIC_RESOURCE_URL = "/api/public/v1/profile/files";
    private static final String TEST_FILE_URL = "https://localhost/api/public/v1/profile/files/olli-opiskelija/test.txt";
    private static final String TEST_FILE_NAME = "test.txt";
    private static final String TEST_FILE_CONTENT = "test";

    @Test
    public void thatProfileFileIsSaved() throws Exception {
        MvcResult result = performPostFile().andExpect(status().isOk())
            .andExpect(jsonPath("$.uploaded").value(true))
            .andExpect(jsonPath("$.fileName").value(TEST_FILE_NAME))
            .andReturn();

        String url = new JSONObject(result.getResponse().getContentAsString()).get("url").toString();
        assertThat(url).matches(TEST_FILE_URL);

        mockMvc.perform(get(PRIVATE_FILES_RESOURCE_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value(getFilePath()));

        performGetFile(getFilePath())
            .andExpect(status().isOk())
            .andExpect(content().string(TEST_FILE_CONTENT));
    }

    @Test
    public void thatProfileFileIsRemoved() throws Exception {
        postAndGetFileUrl();
        performGetFile(getFilePath()).andExpect(status().isOk());

        mockMvc.perform(delete(String.join("/", PRIVATE_FILES_RESOURCE_PATH, TEST_FILE_NAME))
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isNoContent());

        performGetFile(getFilePath()).andExpect(status().isNotFound());
    }

    @Test
    public void thatNoProfileFilesReturnsEmptyFileList() throws Exception {
        mockMvc.perform(get(PRIVATE_FILES_RESOURCE_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void thatPrivateProfileFilesAreReturnedForOwner() throws Exception {
        String url = postAndGetFileUrl();

        mockMvc.perform(get(url)
            .with(securityContext(studentSecurityContext())))
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

    private ResultActions performGetFile(String filePath) throws Exception {
        return mockMvc.perform(get(String.join("/", PUBLIC_RESOURCE_URL, filePath))
            .with(securityContext(studentSecurityContext())));
    }

    private String getFilePath() {
        return String.join("/", "olli-opiskelija", TEST_FILE_NAME);
    }
}
