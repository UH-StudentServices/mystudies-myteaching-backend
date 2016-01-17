package fi.helsinki.opintoni.cache;

import com.google.common.base.Preconditions;
import com.rometools.fetcher.impl.FeedFetcherCache;
import com.rometools.fetcher.impl.SyndFeedInfo;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.Optional;

public class SpringFeedFetcherCache implements FeedFetcherCache {

    private String cacheName;

    private CacheManager cacheManager;

    public SpringFeedFetcherCache(String cacheName, CacheManager cacheManager) {
        this.cacheName = cacheName;
        this.cacheManager = cacheManager;
    }

    private Cache cache;

    @PostConstruct
    public void init() {
        Preconditions.checkNotNull(cacheManager);
        Preconditions.checkNotNull(cacheName);
        cache = cacheManager.getCache(cacheName);
    }

    @Override
    public SyndFeedInfo getFeedInfo(URL feedUrl) {
        return get(feedUrl);
    }

    @Override
    public void setFeedInfo(URL feedUrl, SyndFeedInfo syndFeedInfo) {
        cache.put(feedUrl, syndFeedInfo);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public SyndFeedInfo remove(URL feedUrl) {
        SyndFeedInfo syndFeedInfo = get(feedUrl);
        cache.evict(feedUrl);
        return syndFeedInfo;
    }

    private SyndFeedInfo get(URL feedUrl) {
        Optional<ValueWrapper> valueWrapper = Optional.ofNullable(cache.get(feedUrl));

        return valueWrapper
            .map(w -> (SyndFeedInfo) w.get())
            .orElse(null);
    }
}
