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

package fi.helsinki.opintoni.web.rest.publicapi.portfolio;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import static com.google.common.base.Charsets.UTF_8;
import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicFilesResourceTest extends PublicPortfolioTest {

    private static final String PRIVATE_RESOURCE_URL = "/api/private/v1/portfolio/files";
    private static final String PUBLIC_RESOURCE_URL = "/api/public/v1/portfolio/files/student/en/olli-opiskelija/test.txt";
    private static final String TEST_FILE_NAME = "test.txt";
    private static final String TEST_FILE_CONTENT = "test";

    @Test
    public void thatPublicPortfolioFilesAreReturnedForUnauthenticatedUser() throws Exception {
        addPortfolioFile();
        saveStudentPortfolioAsPublic();

        mockMvc.perform(get(PUBLIC_RESOURCE_URL))
            .andExpect(status().isOk())
            .andExpect(content().string(TEST_FILE_CONTENT));
    }

    private void addPortfolioFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", TEST_FILE_NAME, "text/plain", TEST_FILE_CONTENT.getBytes(UTF_8));
        mockMvc.perform(fileUpload(PRIVATE_RESOURCE_URL).file(file)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isNoContent());
    }
}
