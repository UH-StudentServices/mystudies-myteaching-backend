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

package fi.helsinki.opintoni.service.news;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.dto.NewsDto;
import fi.helsinki.opintoni.integration.newsfeeds.FlammaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class FlammaNewsService extends FetchingNewsService {

    @Autowired
    private FlammaClient flammaClient;

    @Cacheable(value = CacheConstants.STUDENT_NEWS, cacheManager = "transientCacheManager")
    public List<NewsDto> getStudentNews(Locale locale) {
        return getAtomNews(() -> flammaClient.getStudentFeed(locale));
    }

    @Cacheable(value = CacheConstants.TEACHER_NEWS, cacheManager = "transientCacheManager")
    public List<NewsDto> getTeacherNews(Locale locale) {
        return getAtomNews(() -> flammaClient.getTeacherFeed(locale));
    }
}
