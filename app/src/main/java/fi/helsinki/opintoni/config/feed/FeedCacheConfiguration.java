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

package fi.helsinki.opintoni.config.feed;

import com.rometools.fetcher.impl.FeedFetcherCache;
import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.cache.SpringFeedFetcherCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedCacheConfiguration {

    @Autowired
    private CacheManager inMemoryCacheManager;

    @Bean
    public FeedFetcherCache feedFetcherCache() {
        return new SpringFeedFetcherCache(CacheConstants.FEEDS, inMemoryCacheManager);
    }
}
