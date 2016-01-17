package fi.helsinki.opintoni.config.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.fetcher.FeedFetcher;
import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.integration.feed.FeedClient;
import fi.helsinki.opintoni.integration.feed.FeedMockClient;
import fi.helsinki.opintoni.integration.feed.FeedRemoteClient;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedClientConfiguration {

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FeedFetcher feedFetcher;

    @Bean
    public FeedMockClient feedMockClient() {
        return new FeedMockClient(objectMapper);
    }

    @Bean
    public FeedRemoteClient feedRemoteClient() {
        return new FeedRemoteClient(feedFetcher);
    }

    @Bean
    public FeedClient feedClient() {
        return NamedDelegatesProxy.builder(
            FeedClient.class,
            () -> appConfiguration.get("feed.client.implementation"))
            .with("remote", feedRemoteClient())
            .with("mock", feedMockClient())
            .build();
    }
}
