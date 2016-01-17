package fi.helsinki.opintoni.integration.feed;

import com.rometools.fetcher.FeedFetcher;
import com.rometools.rome.feed.synd.SyndFeed;

import java.net.URL;
import java.util.Optional;

public class FeedRemoteClient implements FeedClient {
    private final FeedFetcher feedFetcher;

    public FeedRemoteClient(FeedFetcher feedFetcher) {
        this.feedFetcher = feedFetcher;
    }

    @Override
    public Optional<SyndFeed> getFeed(String feedUrl) {
        Optional<SyndFeed> feed = Optional.empty();
        try {
            feed = Optional.ofNullable(feedFetcher.retrieveFeed(new URL(feedUrl)));
        } catch (Exception e) {}
        return feed;
    }
}
