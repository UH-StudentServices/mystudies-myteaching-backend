package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.dto.FeedDto;
import fi.helsinki.opintoni.integration.feed.FeedClient;
import fi.helsinki.opintoni.service.converter.FeedConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedService {

    private final FeedConverter feedConverter;
    private final FeedClient feedClient;

    @Autowired
    public FeedService(FeedConverter feedConverter, FeedClient feedClient) {
        this.feedConverter = feedConverter;
        this.feedClient = feedClient;
    }

    public FeedDto getFeed(String feedUrl, int limit) {
        return feedClient.getFeed(feedUrl)
            .map(f -> feedConverter.toDto(f, limit))
            .orElseThrow(() -> new RuntimeException("Feed not found"));
    }

}
