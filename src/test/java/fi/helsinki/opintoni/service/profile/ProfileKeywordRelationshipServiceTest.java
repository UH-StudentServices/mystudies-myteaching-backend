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

package fi.helsinki.opintoni.service.profile;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.profile.ProfileKeywordRelationship;
import fi.helsinki.opintoni.dto.profile.KeywordDto;
import fi.helsinki.opintoni.repository.profile.ProfileKeywordRelationshipRepository;
import fi.helsinki.opintoni.web.rest.privateapi.profile.keyword.Keyword;
import fi.helsinki.opintoni.web.rest.privateapi.profile.keyword.UpdateKeywordsRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileKeywordRelationshipServiceTest extends SpringTest {

    private static final Long PROFILE_ID_WITH_NO_KEYWORDS = 1L;
    private static final Long PROFILE_ID_WITH_EXISTING_KEYWORDS = 2L;
    private static final String KEYWORD_TITLE = "Keyword 1";

    @Autowired
    private ProfileKeywordRelationshipService profileKeywordRelationshipService;

    @Autowired
    private ProfileKeywordRelationshipRepository profileKeywordRelationshipRepository;

    @Test
    public void thatProfileKeywordRelationshipsAreCreatedFromExistingKeyword() {
        Keyword keyword = new Keyword();
        keyword.title = KEYWORD_TITLE;

        UpdateKeywordsRequest updateKeywordsRequest = new UpdateKeywordsRequest();
        updateKeywordsRequest.keywords.add(keyword);

        List<KeywordDto> keywordDtos = profileKeywordRelationshipService
            .update(PROFILE_ID_WITH_NO_KEYWORDS, updateKeywordsRequest);

        assertThat(keywordDtos).hasSize(1);
        assertThat(keywordDtos.get(0).title).isEqualTo(KEYWORD_TITLE);
    }

    @Test
    public void thatProfileKeywordRelationshipsAreCreatedFromNewKeyword() {
        Keyword keyword = new Keyword();
        keyword.title = "New keyword";

        UpdateKeywordsRequest updateKeywordsRequest = new UpdateKeywordsRequest();
        updateKeywordsRequest.keywords.add(keyword);

        List<KeywordDto> keywordDtos = profileKeywordRelationshipService
            .update(PROFILE_ID_WITH_NO_KEYWORDS, updateKeywordsRequest);

        assertThat(keywordDtos).hasSize(1);
        assertThat(keywordDtos.get(0).title).isEqualTo("New keyword");
    }

    @Test
    public void thatOldProfileKeywordRelationshipsAreDeleted() {
        Keyword keyword = new Keyword();
        keyword.title = "New keyword";
        keyword.orderIndex = 0;

        UpdateKeywordsRequest updateKeywordsRequest = new UpdateKeywordsRequest();
        updateKeywordsRequest.keywords.add(keyword);

        List<ProfileKeywordRelationship> profileKeywordRelationships = profileKeywordRelationshipRepository
            .findByProfileIdOrderByOrderIndexAsc(PROFILE_ID_WITH_EXISTING_KEYWORDS);

        assertThat(profileKeywordRelationships).hasSize(1);
        assertThat(profileKeywordRelationships.get(0).id).isEqualTo(1L);

        profileKeywordRelationshipService.update(PROFILE_ID_WITH_EXISTING_KEYWORDS, updateKeywordsRequest);

        profileKeywordRelationships = profileKeywordRelationshipRepository
            .findByProfileIdOrderByOrderIndexAsc(PROFILE_ID_WITH_EXISTING_KEYWORDS);

        assertThat(profileKeywordRelationships).hasSize(1);
        assertThat(profileKeywordRelationships.get(0).id).isEqualTo(2L);
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

        profileKeywordRelationshipService.update(PROFILE_ID_WITH_NO_KEYWORDS, updateKeywordsRequest);

        List<ProfileKeywordRelationship> profileKeywordRelationships = profileKeywordRelationshipRepository
            .findByProfileIdOrderByOrderIndexAsc(PROFILE_ID_WITH_NO_KEYWORDS);

        assertThat(profileKeywordRelationships).hasSize(2);
        assertThat(profileKeywordRelationships.get(0).profileKeyword.title).isEqualTo("First keyword");
        assertThat(profileKeywordRelationships.get(1).profileKeyword.title).isEqualTo("Second keyword");
    }
}
