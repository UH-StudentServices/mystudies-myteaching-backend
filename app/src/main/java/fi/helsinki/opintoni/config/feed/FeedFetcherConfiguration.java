package fi.helsinki.opintoni.config.feed;

import com.rometools.fetcher.FeedFetcher;
import com.rometools.fetcher.impl.FeedFetcherCache;
import com.rometools.fetcher.impl.HttpURLFeedFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedFetcherConfiguration {

    @Autowired
    private FeedFetcherCache feedFetcherCache;

    @Bean
    public FeedFetcher feedFetcher() {
        return new HttpURLFeedFetcher(feedFetcherCache);
    }
}
