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

import fi.helsinki.opintoni.domain.portfolio.*;
import fi.helsinki.opintoni.dto.portfolio.SampleDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.repository.portfolio.SampleRepository;
import fi.helsinki.opintoni.service.converter.SampleConverter;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.sample.UpdateSample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

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
        return samples.stream().map(sampleConverter::toDto).collect(toList());
    }

    public List<SampleDto> findByPortfolioIdAndVisibility(Long portfolioId, ComponentVisibility.Visibility visibility) {
        return sampleRepository.findByPortfolioIdAndVisibility(portfolioId, visibility).stream()
            .map(sampleConverter::toDto)
            .collect(toList());
    }

    public List<SampleDto> updateSamples(Long portfolioId, List<UpdateSample> updateSamples) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NotFoundException::new);

        sampleRepository.deleteByPortfolioId(portfolio.id);

        List<Sample> samples = updateSamples.stream()
            .map(updateSample -> sampleConverter.toEntity(updateSample, portfolio))
            .collect(toList());
        sampleRepository.saveAll(samples);

        return findByPortfolioId(portfolioId);
    }
}
