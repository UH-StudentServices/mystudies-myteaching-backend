package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import com.google.common.collect.ImmutableList;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.portfolio.LanguageProficiency;
import fi.helsinki.opintoni.domain.portfolio.PortfolioLanguage;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficienciesChangeDescriptorDto;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import fi.helsinki.opintoni.service.portfolio.LanguageProficiencyService;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.web.WebTestUtils.toJsonBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PrivateLanguageProficiencyResourceTest extends SpringTest {
    private static final String API_PATH = "/portfolio/2/languageproficiencies";
    private static final long PORTFOLIO_ID = 2L;
    private static final long ENGLISH_LANGUAGE_PROFICIENCY_ID = 1L;
    private static final long HINDI_LANGUAGE_PROFICIENCY_ID = 4L;
    private static final long ANOTHER_USERS_LANGUAGE_PROFICIENCY_ID = 3L;

    @Autowired
    private LanguageProficiencyService languageProficiencyService;

    @Test
    public void thatLanguageProficiencyChangesArePersisted() throws Exception {
        LanguageProficienciesChangeDescriptorDto updateBatch = new LanguageProficienciesChangeDescriptorDto();

        LanguageProficiencyDto greek = newLanguageProficiency(PortfolioLanguage.GREEK,
            LanguageProficiency.ELEMENTARY_PROFICIENCY,
            null);
        LanguageProficiencyDto chinese = newLanguageProficiency(PortfolioLanguage.CHINESE,
            LanguageProficiency.LIMITED_WORKING_PROFICIENCY,
            null);

        updateBatch.newLanguageProficiencies = ImmutableList.of(greek, chinese);

        LanguageProficiencyDto english = newLanguageProficiency(PortfolioLanguage.ENGLISH,
            LanguageProficiency.NATIVE_PROFICIENCY,
            ENGLISH_LANGUAGE_PROFICIENCY_ID);

        updateBatch.updatedLanguageProficiencies = ImmutableList.of(english);
        updateBatch.deletedIds = ImmutableList.of(HINDI_LANGUAGE_PROFICIENCY_ID);

        persistUpdateBatchWithStatus(updateBatch, status().isNoContent());

        assertThat(languageProficiencyService.findByPortfolioId(PORTFOLIO_ID))
            .extracting("id", "language", "proficiency")
            .contains(
                tuple(1L, PortfolioLanguage.ENGLISH, LanguageProficiency.NATIVE_PROFICIENCY),
                tuple(2L, PortfolioLanguage.FINNISH, LanguageProficiency.NATIVE_PROFICIENCY),
                tuple(5L, PortfolioLanguage.GREEK, LanguageProficiency.ELEMENTARY_PROFICIENCY),
                tuple(6L, PortfolioLanguage.CHINESE, LanguageProficiency.LIMITED_WORKING_PROFICIENCY)
            );
    }

    @Test
    public void thatAnotherLanguageProficiencyCannotBeInsertedForLanguageForWhichProficiencyAlreadyExists() throws Exception {
        LanguageProficienciesChangeDescriptorDto updateBatch = new LanguageProficienciesChangeDescriptorDto();
        LanguageProficiencyDto english = newLanguageProficiency(PortfolioLanguage.ENGLISH,
            LanguageProficiency.NATIVE_PROFICIENCY,
            null);

        updateBatch.newLanguageProficiencies = ImmutableList.of(english);

        persistUpdateBatchWithStatus(updateBatch, status().isInternalServerError());
    }

    @Test
    public void thatUserCannotUpdateLanguageProficienciesUnderAnothersPortfolio() throws Exception {
        LanguageProficienciesChangeDescriptorDto updateBatch = new LanguageProficienciesChangeDescriptorDto();
        LanguageProficiencyDto swedish = newLanguageProficiency(PortfolioLanguage.SWEDISH,
            LanguageProficiency.FULL_PROFESSIONAL_PROFICIENCY,
            ANOTHER_USERS_LANGUAGE_PROFICIENCY_ID);

        updateBatch.updatedLanguageProficiencies = ImmutableList.of(swedish);

        persistUpdateBatchWithStatus(updateBatch, status().isForbidden());
    }

    @Test
    public void thatUserCannotDeleteLanguageProficienciesUnderAnothersPortfolio() throws Exception {
        LanguageProficienciesChangeDescriptorDto updateBatch = new LanguageProficienciesChangeDescriptorDto();
        updateBatch.deletedIds = ImmutableList.of(ANOTHER_USERS_LANGUAGE_PROFICIENCY_ID);

        persistUpdateBatchWithStatus(updateBatch, status().isForbidden());
    }

    private void persistUpdateBatchWithStatus(LanguageProficienciesChangeDescriptorDto updateBatch,
                                              ResultMatcher status) throws Exception {
        mockMvc.perform(patch(RestConstants.PRIVATE_API_V1 + API_PATH)
            .with(securityContext(studentSecurityContext()))
            .content(toJsonBytes(updateBatch))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status);
    }

    private LanguageProficiencyDto newLanguageProficiency(PortfolioLanguage language,
                                                          LanguageProficiency proficiency,
                                                          Long id) {
        LanguageProficiencyDto dto = new LanguageProficiencyDto();
        dto.language = language;
        dto.proficiency = proficiency;
        dto.id = id;

        return dto;
    }
}
