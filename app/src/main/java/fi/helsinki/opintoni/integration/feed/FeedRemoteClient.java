package fi.helsinki.opintoni.integration.feed;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class FeedRemoteClient implements FeedClient {

    @Override
    public Optional<SyndFeed> getFeed(String feedUrl) {
        Optional<SyndFeed> feed = Optional.empty();
        try {
            SyndFeedInput input = new SyndFeedInput();
            feed = Optional.ofNullable(input.build(new XmlReader(new URL(feedUrl))));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Provided Url is malformed: " + feedUrl);
        } catch (Exception e) {
            throw new RuntimeException("Feed parsing failed: " + feedUrl);
        }
        return feed;
    }
}
