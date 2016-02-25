package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.portfolio.LanguageProficiency;
import fi.helsinki.opintoni.domain.portfolio.PortfolioLanguage;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;
import org.springframework.http.MediaType;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.web.WebTestUtils.toJsonBytes;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateLanguageProficiencyResourceTest extends SpringTest {
    private static final String API_PATH = "/portfolio/2/languageproficiencies";
    private static final String RESOURCE_ID = "/2";

    @Test
    public void thatLanguageProficienciesAreReturned() throws Exception {
        mockMvc.perform(get(RestConstants.PRIVATE_API_V1 + API_PATH)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].languageCode").value("en"))
            .andExpect(jsonPath("$[0].proficiency").value(4))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].languageCode").value("fi"))
            .andExpect(jsonPath("$[1].proficiency").value(5));
    }

    @Test
    public void thatLanguageProficiencyIsInserted() throws Exception {
        LanguageProficiencyDto languageProficiencyDto = new LanguageProficiencyDto();
        languageProficiencyDto.languageCode = PortfolioLanguage.GREEK;
        languageProficiencyDto.proficiency = LanguageProficiency.ELEMENTARY_PROFICIENCY;

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + API_PATH)
            .with(securityContext(studentSecurityContext()))
            .content(toJsonBytes(languageProficiencyDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.languageCode").value("el"))
            .andExpect(jsonPath("$.proficiency").value(1));
    }

    @Test
    public void thatAnotherLanguageProficiencyCannotBeInsertedForLanguageForWhichProficiencyAlreadyExists() throws Exception {
        LanguageProficiencyDto languageProficiencyDto = new LanguageProficiencyDto();
        languageProficiencyDto.languageCode = PortfolioLanguage.ENGLISH;
        languageProficiencyDto.proficiency = LanguageProficiency.NATIVE_PROFICIENCY;

        mockMvc.perform(post(RestConstants.PRIVATE_API_V1 + API_PATH)
            .with(securityContext(studentSecurityContext()))
            .content(toJsonBytes(languageProficiencyDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void thatLanguageProficiencyIsUpdated() throws Exception {
        LanguageProficiencyDto languageProficiencyDto = new LanguageProficiencyDto();
        languageProficiencyDto.languageCode = PortfolioLanguage.FINNISH;
        languageProficiencyDto.proficiency = LanguageProficiency.ELEMENTARY_PROFICIENCY;

        mockMvc.perform(put(RestConstants.PRIVATE_API_V1 + API_PATH + RESOURCE_ID)
            .with(securityContext(studentSecurityContext()))
            .content(toJsonBytes(languageProficiencyDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.languageCode").value("fi"))
            .andExpect(jsonPath("$.proficiency").value(1));
    }

    @Test
    public void thatLanguageProficiencyIsDeleted() throws Exception {
        mockMvc.perform(delete(RestConstants.PRIVATE_API_V1 + API_PATH + RESOURCE_ID)
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isNoContent());
    }
}
