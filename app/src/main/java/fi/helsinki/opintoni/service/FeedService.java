package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.dto.FeedDto;
import fi.helsinki.opintoni.dto.FindFeedDto;
import fi.helsinki.opintoni.integration.feed.FeedClient;
import fi.helsinki.opintoni.integration.pagemetadata.PageMetaDataHttpClient;
import fi.helsinki.opintoni.service.converter.FeedConverter;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FeedService {
    private final static String FEED_CSS_SELECTOR = "link[type=application/rss+xml], link[type=application/atom+xml]";
    private final static String FEED_HREF_ATTRIBUTE = "href";

    private final FeedConverter feedConverter;
    private final FeedClient feedClient;
    private final PageMetaDataHttpClient pageMetaDataHttpClient;

    @Autowired
    public FeedService(FeedConverter feedConverter, FeedClient feedClient, PageMetaDataHttpClient pageMetaDataHttpClient) {
        this.feedConverter = feedConverter;
        this.feedClient = feedClient;
        this.pageMetaDataHttpClient = pageMetaDataHttpClient;
    }

    public FeedDto getFeed(String feedUrl, int limit) {
        return feedClient.getFeed(feedUrl)
            .map(f -> feedConverter.toDto(f, limit))
            .orElse(null);
    }

    public FindFeedDto findRssFeed(String feedUrl) {
        return feedClient
            .getFeed(feedUrl)
            .map(feed -> new FindFeedDto(feed.getTitle(), feedUrl))
            .orElseGet(() -> parseAndRetrieveFeedFromWebPage(feedUrl));
    }

    private FindFeedDto parseAndRetrieveFeedFromWebPage(String feedUrl) {
        return parseFeedUrlFromWebPage(feedUrl)
            .flatMap(parsedUrl ->
                feedClient.getFeed(parsedUrl)
                    .map(feed -> new FindFeedDto(feed.getTitle(), parsedUrl)))
            .orElse(null);
    }

    private Optional<String> parseFeedUrlFromWebPage(String url) {
        return pageMetaDataHttpClient
            .getPageBody(url)
            .map(Jsoup::parse)
            .map(document -> document.select(FEED_CSS_SELECTOR))
            .filter(elements -> !elements.isEmpty())
            .map(elements -> elements.get(0).attr(FEED_HREF_ATTRIBUTE));
    }

}
