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
    private CacheManager cacheManager;

    @Bean
    public FeedFetcherCache feedFetcherCache() {
        return new SpringFeedFetcherCache(CacheConstants.FEEDS, cacheManager);
    }
}
