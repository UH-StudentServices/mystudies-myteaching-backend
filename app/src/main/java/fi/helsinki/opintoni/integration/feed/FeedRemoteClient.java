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

import com.rometools.fetcher.FeedFetcher;
import com.rometools.rome.feed.synd.SyndFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Optional;

public class FeedRemoteClient implements FeedClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedRemoteClient.class);

    private final FeedFetcher feedFetcher;

    public FeedRemoteClient(FeedFetcher feedFetcher) {
        this.feedFetcher = feedFetcher;
    }

    @Override
    public Optional<SyndFeed> getFeed(String feedUrl) {
        Optional<SyndFeed> feed = Optional.empty();
        try {
            feed = Optional.ofNullable(feedFetcher.retrieveFeed(new URL(feedUrl)));
        } catch (Exception e) {
            LOGGER.error("Error when fetching feed from {}", feedUrl, e);
        }
        return feed;
    }
}
