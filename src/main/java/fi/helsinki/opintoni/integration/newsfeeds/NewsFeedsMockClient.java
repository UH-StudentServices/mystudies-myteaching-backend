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

package fi.helsinki.opintoni.integration.newsfeeds;

import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class NewsFeedsMockClient {

    private static final Logger log = LoggerFactory.getLogger(NewsFeedsMockClient.class);

    protected Feed getFeedFromPath(String mockResourcePath) {
        SyndFeedInput feedInput = new SyndFeedInput();
        InputStream inputResource = ClassLoader.class.getResourceAsStream(mockResourcePath);
        try {
            SyndFeed syndFeed = feedInput.build(new XmlReader(inputResource));
            return (Feed) syndFeed.createWireFeed();
        } catch (FeedException | IOException e) {
            log.error(e.getMessage());
        }
        log.warn("Could not get mock data from {}. Returning empty feed.", mockResourcePath);
        return new Feed("atom_0.3");
    }
}
