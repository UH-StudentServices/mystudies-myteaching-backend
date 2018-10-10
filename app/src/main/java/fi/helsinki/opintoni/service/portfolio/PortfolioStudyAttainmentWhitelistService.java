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
import fi.helsinki.opintoni.domain.portfolio.StudyAttainmentWhitelist;
import fi.helsinki.opintoni.domain.portfolio.StudyAttainmentWhitelistEntry;
import fi.helsinki.opintoni.dto.portfolio.StudyAttainmentWhitelistDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioStudyAttainmentWhitelistEntryRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioStudyAttainmentWhitelistRepository;
import fi.helsinki.opintoni.service.DtoService;
import fi.helsinki.opintoni.service.converter.portfolio.StudyAttainmentWhitelistConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@Transactional
public class PortfolioStudyAttainmentWhitelistService extends DtoService {

    private final PortfolioStudyAttainmentWhitelistEntryRepository entryRepository;

    private final PortfolioStudyAttainmentWhitelistRepository whitelistRepository;

    private final StudyAttainmentWhitelistConverter whitelistConverter;

    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioStudyAttainmentWhitelistService(PortfolioStudyAttainmentWhitelistEntryRepository entryRepository,
                                                    PortfolioStudyAttainmentWhitelistRepository whitelistRepository,
                                                    StudyAttainmentWhitelistConverter whitelistConverter,
                                                    PortfolioRepository portfolioRepository) {
        this.entryRepository = entryRepository;
        this.whitelistRepository = whitelistRepository;
        this.whitelistConverter = whitelistConverter;
        this.portfolioRepository = portfolioRepository;
    }

    public StudyAttainmentWhitelistDto get(Long portfolioId) {
        return getDto(portfolioId,
            whitelistRepository::findByPortfolioId,
            whitelistConverter::toDto);
    }

    public void insert(Portfolio portfolio) {
        StudyAttainmentWhitelist whitelist = new StudyAttainmentWhitelist();
        whitelist.portfolio = portfolio;
        whitelist.whitelistEntries = new ArrayList<>();
        whitelistRepository.save(whitelist);
    }

    public StudyAttainmentWhitelistDto update(Long portfolioId, StudyAttainmentWhitelistDto whitelistDto) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NotFoundException::new);
        whitelistRepository.deleteByPortfolioId(portfolioId);
        StudyAttainmentWhitelist whitelist = new StudyAttainmentWhitelist();
        whitelist.portfolio = portfolio;
        whitelistRepository.save(whitelist);
        whitelist.whitelistEntries = whitelistDto.oodiStudyAttainmentIds
            .stream().map(i -> createWhitelistEntry(whitelist, i))
            .collect(Collectors.toList());
        return whitelistConverter.toDto(whitelist);
    }

    private StudyAttainmentWhitelistEntry createWhitelistEntry(StudyAttainmentWhitelist whitelist,
                                                               Long studyAttainmentId) {
        StudyAttainmentWhitelistEntry entry = new StudyAttainmentWhitelistEntry();
        entry.whitelist = whitelist;
        entry.studyAttainmentId = studyAttainmentId;
        return entryRepository.save(entry);
    }

}
