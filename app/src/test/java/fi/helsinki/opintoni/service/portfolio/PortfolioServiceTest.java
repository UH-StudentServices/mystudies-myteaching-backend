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
import fi.helsinki.opintoni.dto.portfolio.PortfolioDto;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class PortfolioServiceTest extends SpringTest {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    public void thatPortfolioIsFoundByPath() {
        PortfolioDto portfolioDto = portfolioService.findByPath("pekka");
        assertEquals("https://opi-1.student.helsinki.fi/portfolio/#/pekka", portfolioDto.url);
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

        assertEquals(updatedOwnerName, updateResult.ownerName);
        assertEquals(updatedIntro, updateResult.intro);
        assertEquals(updatedVisibility, updateResult.visibility);
    }

    @Test
    public void thatPortfolioIsCreated() {
        assertFalse(portfolioRepository.findByUserId(4L).isPresent());

        portfolioService.insert(4L, "Olli Opettaja");

        Portfolio portfolio = portfolioRepository.findByUserId(4L).get();
        assertEquals(PortfolioVisibility.PRIVATE, portfolio.visibility);
    }
}
