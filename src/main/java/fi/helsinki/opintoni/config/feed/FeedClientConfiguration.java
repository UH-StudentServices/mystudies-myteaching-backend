/*
 * This file is part of MystudiesMyteaching application.
 *
 * MystudiesMyteaching application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MystudiesMyteaching application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MystudiesMyteaching application.  If not, see <http://www.gnu.org/licenses/>.
 */

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
