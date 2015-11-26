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

package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.dto.CategoryHitDto;
import fi.helsinki.opintoni.dto.SearchHitDto;
import fi.helsinki.opintoni.integration.leiki.LeikiClient;
import fi.helsinki.opintoni.service.converter.CategoryHitConverter;
import fi.helsinki.opintoni.service.converter.SearchHitConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final LeikiClient leikiClient;
    private final SearchHitConverter searchHitConverter;
    private final CategoryHitConverter categoryHitConverter;

    @Autowired
    public SearchService(LeikiClient leikiClient,
                         SearchHitConverter searchHitConverter,
                         CategoryHitConverter categoryHitConverter) {
        this.leikiClient = leikiClient;
        this.searchHitConverter = searchHitConverter;
        this.categoryHitConverter = categoryHitConverter;
    }

    public List<SearchHitDto> search(String searchTerm, Locale locale) throws Exception {
        return leikiClient.search(searchTerm, locale).stream()
            .map(searchHitConverter::toDto)
            .collect(Collectors.toList());
    }

    public List<CategoryHitDto> searchCategory(String searchTerm, Locale locale) throws Exception {
        return leikiClient.searchCategory(searchTerm, locale).stream()
            .map(categoryHitConverter::toDto)
            .collect(Collectors.toList());
    }

}
