package fi.helsinki.opintoni.service;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.dto.FeedDto;
import fi.helsinki.opintoni.dto.FindFeedDto;
import fi.helsinki.opintoni.integration.feed.FeedClient;
import fi.helsinki.opintoni.integration.pagemetadata.PageMetaDataHttpClient;
import fi.helsinki.opintoni.service.converter.FeedConverter;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<FindFeedDto> findRssFeed(String feedUrl) {
        Optional<List<FindFeedDto>> findFeedDtoOptional = findFeed(feedUrl).map(Lists::newArrayList);

        return findFeedDtoOptional.orElseGet(() -> parseAndRetrieveFeedsFromWebPage(feedUrl));
    }

    private List<FindFeedDto> parseAndRetrieveFeedsFromWebPage(String feedUrl) {
        return parseFeedUrlsFromWebPage(feedUrl)
            .stream()
            .map(parsedFeedUrl -> findFeed(parsedFeedUrl).orElse(null))
            .filter(feedDto -> feedDto != null)
            .collect(Collectors.toList());
    }

    private Optional<FindFeedDto> findFeed(String feedUrl) {
        return feedClient.getFeed(feedUrl)
            .map(feed -> new FindFeedDto(feed.getTitle(), feedUrl));
    }

    private List<String> parseFeedUrlsFromWebPage(String url) {
        return pageMetaDataHttpClient
            .getPageBody(url)
            .map(Jsoup::parse)
            .map(document -> document.select(FEED_CSS_SELECTOR))
            .map(this::getFeedUrls)
            .orElseGet(Lists::newArrayList);
    }

    private List<String> getFeedUrls(Elements elements) {
        return elements
            .stream()
            .map(element -> element.attr(FEED_HREF_ATTRIBUTE))
            .collect(Collectors.toList());
    }

}
