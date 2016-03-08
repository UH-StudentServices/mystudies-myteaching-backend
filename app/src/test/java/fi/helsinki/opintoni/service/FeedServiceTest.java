package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.sampledata.RSSFeedSampleData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class FeedServiceTest extends SpringTest {

    private static final String EXPECTED_FEED_TITLE = "RSS feed";

    @Autowired
    private FeedService feedService;

    @Test
    public void thatIfUrlIsWebPageFeedUrlIsParsedAndFeedRetrieved() {
        webPageServer.expectRssFeedRequest(getMockFeedApiUrl());
        findFeedAndAssertTitle(RSSFeedSampleData.WEBPAGE_CONTAINING_RSS_FEED_URL);
    }

    @Test
    public void thatIfUrlIsFeedItIsRetrieved() {
        findFeedAndAssertTitle(getMockFeedApiUrl());
    }

    private void findFeedAndAssertTitle(String feedUrl) {
        assertThat(feedService.findRssFeed(feedUrl).title)
            .isEqualTo(EXPECTED_FEED_TITLE);
    }

}
