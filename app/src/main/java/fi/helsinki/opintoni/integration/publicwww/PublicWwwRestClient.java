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

package fi.helsinki.opintoni.integration.publicwww;

import com.rometools.rome.feed.rss.Channel;
import fi.helsinki.opintoni.config.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Component
public class PublicWwwRestClient {
    private final static Logger log = LoggerFactory.getLogger(PublicWwwRestClient.class);

    private final String baseUrl;
    private final RestTemplate restTemplate;

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    public PublicWwwRestClient(AppConfiguration appConfiguration) {
        this.baseUrl = appConfiguration.get("publicWww.base.url");
        this.restTemplate = createRestTemplate();
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public Channel getOpenUniversityFeed() {
        String uri = getFeedUri();

        try {
            return restTemplate.getForObject(uri, Channel.class);
        } catch (RestClientException e) {
            log.error("Public WWW client threw exception: {}", e.getMessage());

            return new Channel("rss_2.0");
        }
    }

    private RestTemplate createRestTemplate() {
        final RssChannelHttpMessageConverter converter = new RssChannelHttpMessageConverter();
        return new RestTemplate(Collections.singletonList(converter));
    }

    private String getFeedUri() {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path(appConfiguration.get("publicWww.path"))
            .toUriString();
    }

}

