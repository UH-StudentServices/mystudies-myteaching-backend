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
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.domain.portfolio.PortfolioVisibility;
import fi.helsinki.opintoni.domain.portfolio.TeacherPortfolioSection;
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.repository.portfolio.ComponentVisibilityRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.PortfolioConverter;
import fi.helsinki.opintoni.web.arguments.PortfolioRole;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class PortfolioServiceTest extends SpringTest {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private ComponentVisibilityService componentVisibilityService;

    @Autowired
    private ComponentVisibilityRepository componentVisibilityRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    private static final int TEACHER_PORTFOLIO_SECTION_COUNT = TeacherPortfolioSection.values().length;
    private static final String PUBLIC_VISIBILITY = "PUBLIC";
    private static final String PRIVATE_VISIBILITY = "PRIVATE";

    @Test
    public void thatPortfolioIsFoundByPath() {
        PortfolioDto portfolioDto = portfolioService.findByPathAndLangAndRole("pekka", Language.FI,
            PortfolioRole.STUDENT, PortfolioConverter.ComponentFetchStrategy.PUBLIC);
        assertThat(portfolioDto.url).isEqualTo("/portfolio/fi/pekka");
    }

    @Test
    public void thatPortfolioIsUpdated() {
        String updatedOwnerName = "Updated owner name";
        String updatedIntro = "Updated intro";
        PortfolioVisibility updatedVisibility = PortfolioVisibility.PUBLIC;

        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.ownerName = updatedOwnerName;
        portfolioDto.intro = updatedIntro;
        portfolioDto.visibility = updatedVisibility;

        PortfolioDto updateResult = portfolioService.update(1L, portfolioDto);

        assertThat(updateResult.ownerName).isEqualTo(updatedOwnerName);
        assertThat(updateResult.intro).isEqualTo(updatedIntro);
        assertThat(updateResult.visibility).isEqualTo(updatedVisibility);
    }

    @Test
    public void thatTeacherPortfolioAndComponentVisibilitiesAreCreated() {
        componentVisibilityRepository.deleteAll();
        deleteExistingTeacherPortfolio();

        assertThat(portfolioRepository.findByUserId(4L).count()).isZero();

        portfolioService.insert(4L, "Olli Opettaja", PortfolioRole.TEACHER, Language.FI);

        Portfolio portfolio = portfolioRepository.findByUserId(4L).findFirst().get();
        assertThat(portfolio.visibility).isEqualTo(PortfolioVisibility.PRIVATE);
        assertThat(portfolio.portfolioRole).isEqualTo(PortfolioRole.TEACHER);
        assertThat(componentVisibilityService.findByPortfolioId(portfolio.id))
            .hasSize(TEACHER_PORTFOLIO_SECTION_COUNT)
            .extracting("teacherPortfolioSection", "visibility")
            .contains(
                tuple("BASIC_INFORMATION", PUBLIC_VISIBILITY),
                tuple("RESEARCH", PRIVATE_VISIBILITY),
                tuple("TEACHING", PRIVATE_VISIBILITY),
                tuple("ADMINISTRATION", PRIVATE_VISIBILITY));
    }

    private void deleteExistingTeacherPortfolio() {
        portfolioRepository.deleteById(4L);
    }
}
