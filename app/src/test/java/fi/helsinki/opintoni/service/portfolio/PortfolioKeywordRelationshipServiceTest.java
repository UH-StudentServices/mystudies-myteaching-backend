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

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.portfolio.PortfolioKeywordRelationship;
import fi.helsinki.opintoni.dto.portfolio.KeywordDto;
import fi.helsinki.opintoni.repository.portfolio.PortfolioKeywordRelationshipRepository;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.keyword.Keyword;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.keyword.UpdateKeywordsRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class PortfolioKeywordRelationshipServiceTest extends SpringTest {

    private final static Long PORTFOLIO_ID_WITH_NO_KEYWORDS = 1L;
    private final static Long PORTFOLIO_ID_WITH_EXISTING_KEYWORDS = 2L;
    private final static String KEYWORD_TITLE = "Keyword 1";

    @Autowired
    private PortfolioKeywordRelationshipService portfolioKeywordRelationshipService;

    @Autowired
    private PortfolioKeywordRelationshipRepository portfolioKeywordRelationshipRepository;

    @Test
    public void thatPortfolioKeywordRelationshipsAreCreatedFromExistingKeyword() {
        Keyword keyword = new Keyword();
        keyword.title = KEYWORD_TITLE;

        UpdateKeywordsRequest updateKeywordsRequest = new UpdateKeywordsRequest();
        updateKeywordsRequest.keywords.add(keyword);

        List<KeywordDto> keywordDtos = portfolioKeywordRelationshipService
            .update(PORTFOLIO_ID_WITH_NO_KEYWORDS, updateKeywordsRequest);

        assertEquals(1, keywordDtos.size());
        assertEquals(KEYWORD_TITLE, keywordDtos.get(0).title);
    }

    @Test
    public void thatPortfolioKeywordRelationshipsAreCreatedFromNewKeyword() {
        Keyword keyword = new Keyword();
        keyword.title = "New keyword";

        UpdateKeywordsRequest updateKeywordsRequest = new UpdateKeywordsRequest();
        updateKeywordsRequest.keywords.add(keyword);

        List<KeywordDto> keywordDtos = portfolioKeywordRelationshipService
            .update(PORTFOLIO_ID_WITH_NO_KEYWORDS, updateKeywordsRequest);

        assertEquals(1, keywordDtos.size());
        assertEquals("New keyword", keywordDtos.get(0).title);
    }

    @Test
    public void thatOldPortfolioKeywordRelationshipsAreDeleted() {
        Keyword keyword = new Keyword();
        keyword.title = "New keyword";
        keyword.orderIndex = 0;

        UpdateKeywordsRequest updateKeywordsRequest = new UpdateKeywordsRequest();
        updateKeywordsRequest.keywords.add(keyword);

        List<PortfolioKeywordRelationship> portfolioKeywordRelationships = portfolioKeywordRelationshipRepository
            .findByPortfolioIdOrderByOrderIndexAsc(PORTFOLIO_ID_WITH_EXISTING_KEYWORDS);

        assertEquals(1, portfolioKeywordRelationships.size());
        assertEquals(new Long(1), portfolioKeywordRelationships.get(0).id);

        portfolioKeywordRelationshipService.update(PORTFOLIO_ID_WITH_EXISTING_KEYWORDS, updateKeywordsRequest);

        portfolioKeywordRelationships = portfolioKeywordRelationshipRepository
            .findByPortfolioIdOrderByOrderIndexAsc(PORTFOLIO_ID_WITH_EXISTING_KEYWORDS);

        assertEquals(1, portfolioKeywordRelationships.size());
        assertEquals(new Long(2), portfolioKeywordRelationships.get(0).id);
    }

    @Test
    public void thatOrderIndexesAreSaved() {
        Keyword firstKeyword = new Keyword();
        firstKeyword.title = "First keyword";
        firstKeyword.orderIndex = 0;

        Keyword secondKeyword = new Keyword();
        secondKeyword.title = "Second keyword";
        secondKeyword.orderIndex = 1;

        UpdateKeywordsRequest updateKeywordsRequest = new UpdateKeywordsRequest();
        updateKeywordsRequest.keywords = Lists.newArrayList(firstKeyword, secondKeyword);

        portfolioKeywordRelationshipService.update(PORTFOLIO_ID_WITH_NO_KEYWORDS, updateKeywordsRequest);

        List<PortfolioKeywordRelationship> portfolioKeywordRelationships = portfolioKeywordRelationshipRepository
            .findByPortfolioIdOrderByOrderIndexAsc(PORTFOLIO_ID_WITH_NO_KEYWORDS);

        assertEquals(2, portfolioKeywordRelationships.size());
        assertEquals("First keyword", portfolioKeywordRelationships.get(0).portfolioKeyword.title);
        assertEquals("Second keyword", portfolioKeywordRelationships.get(1).portfolioKeyword.title);
    }
}
