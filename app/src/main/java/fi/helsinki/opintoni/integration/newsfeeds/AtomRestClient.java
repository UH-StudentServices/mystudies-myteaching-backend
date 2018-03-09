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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class AtomRestClient {
    private static final Logger log = LoggerFactory.getLogger(AtomRestClient.class);

    private final RestTemplate restTemplate;

    public AtomRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    protected Feed getFeed(String uri) {
        try {
            return restTemplate.getForObject(uri, Feed.class);
        } catch (RestClientException e) {
            log.error("REST client with uri {} threw exception: {}", uri, e.getMessage());
            return new Feed("atom_0.3");
        }
    }
}
