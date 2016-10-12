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

import fi.helsinki.opintoni.domain.portfolio.JobSearch;
import fi.helsinki.opintoni.dto.portfolio.JobSearchDto;
import fi.helsinki.opintoni.repository.portfolio.JobSearchRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.JobSearchConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class JobSearchService {

    private final JobSearchRepository jobSearchRepository;
    private final PortfolioRepository portfolioRepository;
    private final JobSearchConverter jobSearchConverter;

    @Autowired
    public JobSearchService(JobSearchRepository jobSearchRepository,
                            JobSearchConverter jobSearchConverter,
                            PortfolioRepository portfolioRepository) {
        this.jobSearchRepository = jobSearchRepository;
        this.portfolioRepository = portfolioRepository;
        this.jobSearchConverter = jobSearchConverter;
    }

    public JobSearchDto findByPortfolioId(Long portfolioId) {
        return jobSearchRepository.findByPortfolioId(portfolioId)
            .map(jobSearchConverter::toDto)
            .orElse(null);
    }

    public JobSearchDto insert(Long portfolioId, JobSearchDto jobSearchDto) {
        JobSearch jobSearch = new JobSearch();
        jobSearch.contactEmail = jobSearchDto.contactEmail;
        jobSearch.headline = jobSearchDto.headline;
        jobSearch.text = jobSearchDto.text;
        jobSearch.portfolio = portfolioRepository.findOne(portfolioId);
        return jobSearchConverter.toDto(jobSearchRepository.save(jobSearch));
    }

    public void delete(Long portfolioId) {
        Optional<JobSearch> jobSearchOptional = jobSearchRepository.findByPortfolioId(portfolioId);
        if (jobSearchOptional.isPresent()) {
            JobSearch jobSearch = jobSearchOptional.get();
            jobSearchRepository.delete(jobSearch.id);
        }
    }
}
