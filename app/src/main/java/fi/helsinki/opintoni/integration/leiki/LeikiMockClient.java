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

package fi.helsinki.opintoni.integration.leiki;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LeikiMockClient implements LeikiClient {

    @Value("classpath:sampledata/leiki/searchresults.json")
    private Resource searchResultsResource;

    @Value("classpath:sampledata/leiki/categoryresults.json")
    private Resource categoryResultsResource;

    @Value("classpath:sampledata/leiki/courserecommendationresults.json")
    private Resource courseRecommendationResultsResource;

    private final ObjectMapper objectMapper;

    public LeikiMockClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<LeikiSearchHit> search(String searchTerm, Locale locale) {
        return getLeikiSearchResponse(searchResultsResource, new TypeReference<LeikiResponse<LeikiSearchHit>>() {
        });
    }

    @Override
    public List<LeikiCategoryHit> searchCategory(String searchTerm, Locale locale) {
        return getLeikiCategoryResponse(categoryResultsResource,
            new TypeReference<LeikiCategoryResponse<LeikiCategoryHit>>() {
            });
    }

    @Override
    public List<LeikiCourseRecommendation> getCourseRecommendations(String studentNumber) {
        return getLeikiSearchResponse(courseRecommendationResultsResource, new TypeReference<LeikiResponse<LeikiCourseRecommendation>>() {
        });
    }

    public <T> List<T> getLeikiSearchResponse(Resource resource, TypeReference<LeikiResponse<T>> typeReference) {
        try {
            LeikiResponse<T> response = objectMapper.readValue(resource.getInputStream(), typeReference);
            return response.data.items;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> getLeikiCategoryResponse(Resource resource, TypeReference<LeikiCategoryResponse<T>>
        typeReference) {
        try {
            LeikiCategoryResponse<T> response = objectMapper.readValue(resource.getInputStream(), typeReference);
            return response.data.matches.get(0).match;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
