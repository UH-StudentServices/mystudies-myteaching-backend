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

import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.domain.portfolio.Sample;
import fi.helsinki.opintoni.dto.portfolio.SampleDto;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.repository.portfolio.SampleRepository;
import fi.helsinki.opintoni.service.converter.SampleConverter;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.sample.UpdateSample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SampleService {

    private final SampleRepository sampleRepository;
    private final PortfolioRepository portfolioRepository;
    private final SampleConverter sampleConverter;

    @Autowired
    public SampleService(SampleRepository sampleRepository,
                         PortfolioRepository portfolioRepository,
                         SampleConverter sampleConverter) {
        this.sampleRepository = sampleRepository;
        this.portfolioRepository = portfolioRepository;
        this.sampleConverter = sampleConverter;
    }

    public List<SampleDto> findByPortfolioId(Long portfolioId) {
        List<Sample> samples = sampleRepository.findByPortfolioId(portfolioId);
        return samples.stream().map(sampleConverter::toDto).collect(Collectors.toList());
    }

    public List<SampleDto> updateSamples(Long portfolioId, List<UpdateSample> updateSamples) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);

        sampleRepository.deleteByPortfolioId(portfolio.id);

        updateSamples.forEach(updateSample -> {
            Sample sample = new Sample();
            sample.title = updateSample.title;
            sample.description = updateSample.description;
            sample.portfolio = portfolio;

            sampleRepository.save(sample);
        });

        return findByPortfolioId(portfolioId);
    }
}
