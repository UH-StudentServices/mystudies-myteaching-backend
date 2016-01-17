package fi.helsinki.opintoni.server;

import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("test")
@RequestMapping(
    value = RestConstants.PUBLIC_API_V1 + "/mockfeed",
    produces = WebConstants.APPLICATION_XML)
public class FeedServer {
    @Value("classpath:sampledata/feed/feed.rss")
    private Resource mockFeed;

    @RequestMapping(method = RequestMethod.GET)
    public String feed() {
        String feed = null;
        try {
            feed = IOUtils.toString(mockFeed.getInputStream());
        } catch (Exception e) {
        }
        return feed;
    }
}
