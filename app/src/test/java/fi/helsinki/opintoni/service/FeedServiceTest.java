package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.sampledata.OpenGraphSampleData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

public class FeedServiceTest extends SpringTest {
    @Autowired
    private FeedService feedService;

    @Test
    public void thatFeedUrlIsRetrievedFromWebPage() {
        metaDataServer.expectMetaDataRequest();

        assertThat(feedService.urlToFeedUrl(OpenGraphSampleData.URL))
            .isEqualTo("http://helsinki.fi/feed");
    }

    @Test
    public void thatOriginalUrlIsReturnedIfItIsValidFeed() {
        String feedUrl = getMockFeedApiUrl();

        assertThat(feedService.urlToFeedUrl(feedUrl))
            .isEqualTo(feedUrl);
    }

}
