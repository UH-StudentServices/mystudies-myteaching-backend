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

package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.sampledata.RSSFeedSampleData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class FeedServiceTest extends SpringTest {

    private static final String FEED_1_ID = "1";
    private static final String FEED_2_ID = "2";
    private static final String EXPECTED_FEED_TITLE = "RSS feed";

    @Autowired
    private FeedService feedService;

    @Test
    public void thatIfUrlIsWebPageFeedUrlIsParsedAndFeedRetrieved() {
        webPageServer.expectRssFeedRequest(getMockFeedApiUrl(FEED_1_ID), getMockFeedApiUrl(FEED_2_ID));
        findFeedAndAssertTitle(RSSFeedSampleData.WEBPAGE_CONTAINING_RSS_FEED_URL);
    }

    @Test
    public void thatIfUrlIsFeedItIsRetrieved() {
        findFeedAndAssertTitle(getMockFeedApiUrl(FEED_1_ID));
    }

    private void findFeedAndAssertTitle(String feedUrl) {
        assertThat(feedService.findRssFeed(feedUrl).get(0).title)
            .isEqualTo(EXPECTED_FEED_TITLE);
    }

}
