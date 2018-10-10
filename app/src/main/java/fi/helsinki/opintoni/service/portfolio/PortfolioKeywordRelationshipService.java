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

import fi.helsinki.opintoni.domain.portfolio.PortfolioKeyword;
import fi.helsinki.opintoni.domain.portfolio.PortfolioKeywordRelationship;
import fi.helsinki.opintoni.dto.portfolio.KeywordDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.portfolio.PortfolioKeywordRelationshipRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioKeywordRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.PortfolioKeywordRelationshipConverter;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.keyword.UpdateKeywordsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PortfolioKeywordRelationshipService {

    private final PortfolioKeywordRelationshipRepository portfolioKeywordRelationshipRepository;
    private final PortfolioKeywordRelationshipConverter portfolioKeywordRelationshipConverter;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioKeywordRepository portfolioKeywordRepository;

    @Autowired
    public PortfolioKeywordRelationshipService(
        PortfolioKeywordRelationshipRepository portfolioKeywordRelationshipRepository,
        PortfolioKeywordRelationshipConverter portfolioKeywordRelationshipConverter,
        PortfolioRepository portfolioRepository,
        PortfolioKeywordRepository portfolioKeywordRepository) {
        this.portfolioKeywordRelationshipRepository = portfolioKeywordRelationshipRepository;
        this.portfolioKeywordRelationshipConverter = portfolioKeywordRelationshipConverter;
        this.portfolioRepository = portfolioRepository;
        this.portfolioKeywordRepository = portfolioKeywordRepository;
    }

    public List<KeywordDto> findByPortfolioId(Long portfolioId) {
        return portfolioKeywordRelationshipRepository.findByPortfolioIdOrderByOrderIndexAsc(portfolioId)
            .stream()
            .map(portfolioKeywordRelationshipConverter::toDto)
            .collect(Collectors.toList());
    }

    public List<KeywordDto> update(Long portfolioId,
                                   UpdateKeywordsRequest updateKeywordsRequest) {
        List<PortfolioKeywordRelationship> portfolioKeywordRelationships = createPortfolioKeywordRelationships(
            portfolioId,
            updateKeywordsRequest);

        portfolioKeywordRelationshipRepository.deleteByPortfolioId(portfolioId);

        return portfolioKeywordRelationshipRepository.saveAll(portfolioKeywordRelationships)
            .stream()
            .map(portfolioKeywordRelationshipConverter::toDto)
            .collect(Collectors.toList());
    }

    private List<PortfolioKeywordRelationship> createPortfolioKeywordRelationships(
        Long portfolioId,
        UpdateKeywordsRequest updateKeywordsRequest) {
        return updateKeywordsRequest.keywords.stream()
            .distinct()
            .map(keyword -> {
                PortfolioKeywordRelationship portfolioKeywordRelationship = new PortfolioKeywordRelationship();
                portfolioKeywordRelationship.portfolioKeyword = obtainPortfolioKeyword(keyword.title);
                portfolioKeywordRelationship.portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NotFoundException::new);
                portfolioKeywordRelationship.orderIndex = keyword.orderIndex;
                return portfolioKeywordRelationship;
            })
            .collect(Collectors.toList());
    }

    private PortfolioKeyword obtainPortfolioKeyword(String title) {
        return portfolioKeywordRepository.findByTitle(title).orElse(new PortfolioKeyword(title));
    }
}
