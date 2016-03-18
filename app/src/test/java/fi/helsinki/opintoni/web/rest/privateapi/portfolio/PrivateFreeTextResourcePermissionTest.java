package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.portfolio.FreeTextContentDto;
import fi.helsinki.opintoni.web.WebTestUtils;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class PrivateFreeTextResourcePermissionTest extends SpringTest {

    private static final String API_PATH_OWN_PORTFOLIO = "/portfolio/2/freetextcontent";

    private static final String API_PATH_TO_OTHER_USERS_PORTFOLIO = "/portfolio/1/freetextcontent";
    private static final String API_PATH_OHTER_USERS_FREETEXTCONTENT_ID = "/2";

    @Test
    public void thatUserCannotInsertFreeTextContentToPortfolioSheDoesNowOwn() throws Exception {
        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + API_PATH_TO_OTHER_USERS_PORTFOLIO)
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatUserCannotUpdateFreeTextContentOfPortfolioSheDoesNowOwn() throws Exception {
        mockMvc.perform(put(RestConstants.PRIVATE_API_V1 + API_PATH_TO_OTHER_USERS_PORTFOLIO + API_PATH_OHTER_USERS_FREETEXTCONTENT_ID)
            .content(WebTestUtils.toJsonBytes(new FreeTextContentDto()))
            .contentType(MediaType.APPLICATION_JSON)
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatUserCannotDeleteFreeTextContentFromPortfolioSheDoesNowOwn() throws Exception {
        mockMvc.perform(delete(RestConstants.PRIVATE_API_V1 + API_PATH_TO_OTHER_USERS_PORTFOLIO + API_PATH_OHTER_USERS_FREETEXTCONTENT_ID)
            .with(securityContext(teacherSecurityContext())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void thatUserCannotUpdateFreeTextContentSheDoesNowOwn() throws Exception {
        mockMvc.perform(put(RestConstants.PRIVATE_API_V1 + API_PATH_OWN_PORTFOLIO + API_PATH_OHTER_USERS_FREETEXTCONTENT_ID)
            .content(WebTestUtils.toJsonBytes(new FreeTextContentDto()))
            .contentType(MediaType.APPLICATION_JSON)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatUserCannotDeleteFreeTextContentSheDoesNowOwn() throws Exception {
        mockMvc.perform(delete(RestConstants.PRIVATE_API_V1 + API_PATH_OWN_PORTFOLIO + API_PATH_OHTER_USERS_FREETEXTCONTENT_ID)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isForbidden());
    }
}
