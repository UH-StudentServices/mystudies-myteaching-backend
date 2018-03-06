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

package fi.helsinki.opintoni.integration.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.Optional;

public class FeedMockClient implements FeedClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedMockClient.class);

    @Value("classpath:sampledata/feed/feed1.rss")
    private Resource mockFeed;

    private final ObjectMapper objectMapper;

    @Autowired
    public FeedMockClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<SyndFeed> getFeed(String feedUrl) {
        Optional<SyndFeed> feed = Optional.empty();
        try {
            SyndFeedInput input = new SyndFeedInput();
            feed = Optional.ofNullable(input.build(new XmlReader(mockFeed.getInputStream())));
        } catch (Exception e) {
            LOGGER.error("Error when fetching feed mock", e);
        }
        return feed;
    }
}
