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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AtomRestClient {
    private final static Logger log = LoggerFactory.getLogger(AtomRestClient.class);

    private final RestTemplate restTemplate;

    @Autowired
    public AtomRestClient() {
        this.restTemplate = createRestTemplate();
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    private RestTemplate createRestTemplate() {
        final AtomFeedHttpMessageConverter converter = new AtomFeedHttpMessageConverter();
        List<MediaType> mediaTypes = Collections
            .unmodifiableList(Arrays.asList(
                MediaType.TEXT_XML,
                new MediaType("application", "rss+xml")
                )
            );

        converter.setSupportedMediaTypes(mediaTypes);

        return new RestTemplate(Collections.singletonList(converter));
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
