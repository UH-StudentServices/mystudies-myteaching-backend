package fi.helsinki.opintoni.integration.feed;

import com.rometools.rome.feed.synd.SyndFeed;

import java.util.Optional;

public interface FeedClient {
    Optional<SyndFeed> getFeed(String feedUrl);
}
