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
import fi.helsinki.opintoni.dto.portfolio.SampleDto;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.sample.UpdateSample;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SampleServiceTest extends SpringTest {

    @Autowired
    private SampleService sampleService;

    @Test
    public void thatSampleIsSaved() {
        Long portfolioId = 3L;

        UpdateSample sample = new UpdateSample();
        sample.url = "www.sample.invalid";
        sample.title = "Sample title";
        sample.description = "This is a longer description of the sample.";

        sampleService.updateSamples(portfolioId, Collections.singletonList(sample));

        List<SampleDto> sampleDtos = sampleService.findByPortfolioId(portfolioId);

        SampleDto savedSampleDto = sampleDtos.get(0);

        assertThat(sampleDtos).hasSize(1);
        assertThat(savedSampleDto.url).isEqualTo(sample.url);
        assertThat(savedSampleDto.title).isEqualTo(sample.title);
        assertThat(savedSampleDto.description).isEqualTo(sample.description);
    }
}
