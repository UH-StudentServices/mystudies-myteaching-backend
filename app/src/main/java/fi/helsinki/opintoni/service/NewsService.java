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


import com.rometools.rome.feed.atom.Feed;
import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.dto.NewsDto;
import fi.helsinki.opintoni.integration.flamma.FlammaRestClient;
import fi.helsinki.opintoni.service.converter.NewsConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final FlammaRestClient flammaRestClient;
    private final NewsConverter newsConverter;

    @Autowired
    public NewsService(FlammaRestClient flammaRestClient, NewsConverter newsConverter) {
        this.flammaRestClient = flammaRestClient;
        this.newsConverter = newsConverter;
    }

    @Cacheable(CacheConstants.STUDENT_NEWS)
    public List<NewsDto> getStudentNews(Locale locale) {
        return getNews(() -> flammaRestClient.getStudentFeed(locale));
    }

    @Cacheable(CacheConstants.TEACHER_NEWS)
    public List<NewsDto> getTeacherNews(Locale locale) {
        return getNews(() -> flammaRestClient.getTeacherFeed(locale));
    }

    @Cacheable(CacheConstants.STUDENT_OPEN_UNIVERSITY_NEWS)
    public List<NewsDto> getStudentOpenUniversityNews(Locale locale) {
        return getNews(() -> flammaRestClient.getStudentOpenUniversityFeed(locale));
    }

    private List<NewsDto> getNews(Supplier<Feed> feedSupplier) {
        return feedSupplier.get().getEntries().stream()
            .limit(4)
            .map(newsConverter::toDto)
            .collect(Collectors.toList());
    }

}
