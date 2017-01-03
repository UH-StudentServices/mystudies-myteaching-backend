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

import fi.helsinki.opintoni.config.SlugifyConfiguration;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.portfolio.PortfolioPathGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioPathGeneratorTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    private PortfolioPathGenerator portfolioPathGenerator;

    @Before
    public void setup() throws IOException {
        SlugifyConfiguration slugifyConfiguration = new SlugifyConfiguration();
        portfolioPathGenerator = new PortfolioPathGenerator(portfolioRepository, slugifyConfiguration.slugify());
    }

    @Test
    public void thatNonConflictingPortfolioPathIsGenerated() {
        assertThat(portfolioPathGenerator.create("Test Name")).isEqualTo("test-name");
        assertThat(portfolioPathGenerator.create(" Test  Name ")).isEqualTo("test-name");
        assertThat(portfolioPathGenerator.create("Test Middle Name")).isEqualTo("test-middle-name");
        assertThat(portfolioPathGenerator.create("Test Middle Name")).isEqualTo("test-middle-name");
        assertThat(portfolioPathGenerator.create("jeanne d'arc")).isEqualTo("jeanne-d-arc");
        assertThat(portfolioPathGenerator.create("Jönssi Mörköperä")).isEqualTo("jonssi-morkopera");
    }

    @Test
    public void thatConflictingPortfolioPathIsGenerated() {
        when(portfolioRepository.countByPath("test-name")).thenReturn(1);
        assertThat(portfolioPathGenerator.create("Test Name")).isEqualTo("test-name-1");
    }

    @Test(expected = RuntimeException.class)
    public void thatExceptionIsThrownIfPathGenerationFails() {
        when(portfolioRepository.countByPath(anyString())).thenReturn(1);
        portfolioPathGenerator.create("Test Name");
    }

    @Test
    public void thatNullIsHandled() {
        assertThat(portfolioPathGenerator.create(null)).isNull();
    }
}
