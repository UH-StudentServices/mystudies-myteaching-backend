package fi.helsinki.opintoni.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.helsinki.opintoni.integration.feed.FeedClient;
import fi.helsinki.opintoni.integration.feed.FeedMockClient;
import fi.helsinki.opintoni.integration.feed.FeedRemoteClient;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedConfiguration {

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public FeedMockClient feedMockClient() {
        return new FeedMockClient(objectMapper);
    }

    @Bean
    public FeedRemoteClient feedRemoteClient() {
        return new FeedRemoteClient();
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
