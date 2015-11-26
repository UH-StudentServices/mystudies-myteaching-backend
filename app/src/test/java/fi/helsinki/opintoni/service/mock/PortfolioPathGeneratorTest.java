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

package fi.helsinki.opintoni.service.mock;

import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.portfolio.PortfolioPathGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioPathGeneratorTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioPathGenerator portfolioPathGenerator;

    @Test
    public void thatNonConflictingPortfolioPathIsGenerated() {
        assertEquals("test.name", portfolioPathGenerator.create("Test Name"));
        assertEquals("test.name", portfolioPathGenerator.create(" Test  Name "));
        assertEquals("test.middle.name", portfolioPathGenerator.create("Test Middle Name"));
        assertEquals("test.middle.name", portfolioPathGenerator.create("Test Middle Name"));
        assertEquals("jeanne.d.arc", portfolioPathGenerator.create("jeanne d'arc"));
    }

    @Test
    public void thatConflictingPortfolioPathIsGenerated() {
        when(portfolioRepository.countByPath("test.name")).thenReturn(1);
        assertEquals("test.name-1", portfolioPathGenerator.create("Test Name"));
    }

    @Test(expected = RuntimeException.class)
    public void thatExceptionIsThrownIfPathGenerationFails() {
        when(portfolioRepository.countByPath(anyString())).thenReturn(1);
        portfolioPathGenerator.create("Test Name");
    }

    @Test
    public void thatNullIsHandled() {
        assertNull(portfolioPathGenerator.create(null));
    }
}
