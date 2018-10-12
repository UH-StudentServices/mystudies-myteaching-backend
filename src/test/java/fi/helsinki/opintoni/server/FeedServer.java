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

package fi.helsinki.opintoni.server;

import com.google.common.collect.ImmutableMap;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Map;

@RestController
@Profile("test")
@RequestMapping(
    value = RestConstants.PUBLIC_API_V1 + "/mockfeed",
    produces = WebConstants.APPLICATION_XML)
public class FeedServer {
    @Value("classpath:sampledata/feed/feed1.rss")
    private Resource mockFeed1;

    @Value("classpath:sampledata/feed/feed2.rss")
    private Resource mockFeed2;

    private Map<String, Resource> feeds;

    @PostConstruct
    private void init() {
        feeds = ImmutableMap.of("1", mockFeed1, "2", mockFeed2);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String feed(@RequestParam("id") String feedId) {
        try {
            return IOUtils.toString(feeds.get(feedId).getInputStream());
        } catch (Exception e) {
            throw new NotFoundException("Feed not found for id: " + feedId);
        }
    }
}
