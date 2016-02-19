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

package fi.helsinki.opintoni.service.portfolio;


import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.portfolio.StudyAttainmentWhitelist;
import fi.helsinki.opintoni.dto.portfolio.StudyAttainmentWhitelistDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.portfolio.PortfolioStudyAttainmentWhitelistRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class PortfolioStudyAttainmentWhitelistServiceTest extends SpringTest {

    private final static Long PORTFOLIO_ID = 1L;
    private static final Long OODI_ATTAINMENT_ID_1 = 1L;
    private static final Long OODI_ATTAINMENT_ID_2 = 2L;
    private static final Long OODI_ATTAINMENT_ID_3 = 3L;

    @Autowired
    private PortfolioStudyAttainmentWhitelistService whitelistService;

    @Autowired
    private PortfolioStudyAttainmentWhitelistRepository whitelistRepository;

    @Test
    @Transactional
    public void shouldUpdatePortfolioWhitelist() {
        StudyAttainmentWhitelistDto whitelistDto = new StudyAttainmentWhitelistDto();
        whitelistDto.oodiStudyAttainmentIds = Arrays.asList(OODI_ATTAINMENT_ID_1, OODI_ATTAINMENT_ID_2);
        whitelistService.update(PORTFOLIO_ID, whitelistDto);

        StudyAttainmentWhitelist whitelist = whitelistRepository.findByPortfolioId(PORTFOLIO_ID).get();
        assertThat(whitelist.whitelistEntries.get(0).studyAttainmentId).isEqualTo(OODI_ATTAINMENT_ID_1);
        assertThat(whitelist.whitelistEntries.get(1).studyAttainmentId).isEqualTo(OODI_ATTAINMENT_ID_2);

        whitelistDto.oodiStudyAttainmentIds = Collections.singletonList(OODI_ATTAINMENT_ID_3);
        whitelistService.update(PORTFOLIO_ID, whitelistDto);

        whitelist = whitelistRepository.findByPortfolioId(PORTFOLIO_ID).get();
        assertThat(whitelist.whitelistEntries.get(0).studyAttainmentId).isEqualTo(OODI_ATTAINMENT_ID_3);
    }

    @Test
    public void shouldGetStudyAttaimentWhitelist() {
        StudyAttainmentWhitelistDto studyAttainmentWhitelistDto = whitelistService.get(2L);
        assertThat(studyAttainmentWhitelistDto).isNotNull();
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundOnMissingWhitelist() {
        whitelistService.get(999L);
    }


}
