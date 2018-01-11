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

package fi.helsinki.opintoni.web.rest.privateapi;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.sampledata.RSSFeedSampleData;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.WebTestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Locale;

import static fi.helsinki.opintoni.security.SecurityRequestPostProcessors.securityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.studentSecurityContext;
import static fi.helsinki.opintoni.security.TestSecurityContext.teacherSecurityContext;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FavoriteResourceTest extends SpringTest {

    @Autowired
    Environment environment;

    @Test
    public void thatTwitterFavoriteIsSaved() throws Exception {
        InsertTwitterFavoriteRequest request = new InsertTwitterFavoriteRequest();
        request.value = "helsinkiuni";
        request.feedType = "USER_TIMELINE";

        mockMvc.perform(post("/api/private/v1/favorites/twitter").with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebTestUtils.toJsonBytes(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(any(Number.class)))
            .andExpect(jsonPath("$.type").value("TWITTER"))
            .andExpect(jsonPath("$.value").value("helsinkiuni"))
            .andExpect(jsonPath("$.feedType").value("USER_TIMELINE"));
    }

    @Test
    public void thatUnisportFavoriteIsSaved() throws Exception {
        mockMvc.perform(post("/api/private/v1/favorites/unisport").with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(any(Number.class)));
    }

    @Test
    public void thatUnisportReservationsAreReturned() throws Exception {
        unisportServer.expectAuthorization();
        unisportServer.expectUserReservations();

        mockMvc.perform(get("/api/private/v1/favorites/unisport").with(securityContext(studentSecurityContext()))
            .locale(new Locale("fi"))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.events[2].name").value("Testikurssin tapahtuma"));
    }

    @Test
    public void thatUnisportAuthorizationUrlIsReturned() throws Exception {
        unisportServer.expectAuthorizationFailWith404();

        mockMvc.perform(get("/api/private/v1/favorites/unisport").with(securityContext(teacherSecurityContext()))
            .locale(new Locale("fi"))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.authorizationUrl").value("https://unisport.fi/ext/opintoni/authorization"));
    }

    @Test
    public void thatRssFavoriteIsSaved() throws Exception {
        SaveRssFavoriteRequest request = new SaveRssFavoriteRequest();
        request.url = "http://rssfeed.com";

        mockMvc.perform(post("/api/private/v1/favorites/rss").with(securityContext(studentSecurityContext()))
            .content(WebTestUtils.toJsonBytes(request))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(any(Number.class)))
            .andExpect(jsonPath("$.url").value("http://rssfeed.com"));
    }

    @Test
    public void thatFavoritesAreReturned() throws Exception {
        mockMvc.perform(get("/api/private/v1/favorites").with(securityContext(studentSecurityContext()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(9))
            .andExpect(jsonPath("$[1].id").value(10));
    }

    @Test
    public void thatFavoritesAreDeleted() throws Exception {
        mockMvc.perform(delete("/api/private/v1/favorites/9")
            .with(securityContext(studentSecurityContext())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void thatRSSFeedIsReturned() throws Exception {
        String feedUrl = getMockFeedApiUrl();

        String requestUrl = String.format("/api/private/v1/favorites/rss?url=%s&limit=3", feedUrl);

        mockMvc.perform(get(requestUrl).with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("RSS feed"))
            .andExpect(jsonPath("$.link").value("http://www.helsinki.fi"))
            .andExpect(jsonPath("$.entries[0].title").value("Feed entry title"))
            .andExpect(jsonPath("$.entries[0].link").value("http://www.helsinki.fi/entry"))
            .andExpect(jsonPath("$.entries[0].description").value("Feed description"))
            .andExpect(jsonPath("$.entries[0].date[0]").value(2015))
            .andExpect(jsonPath("$.entries[0].date[1]").value(12))
            .andExpect(jsonPath("$.entries[0].date[2]").value(4))
            .andExpect(jsonPath("$.entries[0].date[3]").value(11))
            .andExpect(jsonPath("$.entries[0].date[4]").value(8))
            .andExpect(jsonPath("$.entries[0].date[5]").value(42));
    }

    @Test
    public void thatRSSFeedIsFoundWhenUrlIsFeedUrl() throws Exception {
        String feedUrl = getMockFeedApiUrl();

        findRssFeed(feedUrl)
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title").value("RSS feed"))
            .andExpect(jsonPath("$[0].url").value(feedUrl));
    }

    @Test
    public void thatRSSFeedIsFoundWhenUrlIsWebPageContainingFeedUrl() throws Exception {
        String feedUrl1 = getMockFeedApiUrl(RSSFeedSampleData.FEED_ID_1);
        String feedUrl2 = getMockFeedApiUrl(RSSFeedSampleData.FEED_ID_2);

        webPageServer.expectRssFeedRequest(feedUrl1, feedUrl2);

        findRssFeed(RSSFeedSampleData.WEBPAGE_CONTAINING_RSS_FEED_URL)
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].title").value("RSS feed"))
            .andExpect(jsonPath("$[0].url").value(feedUrl1))
            .andExpect(jsonPath("$[1].title").value("RSS feed 2"))
            .andExpect(jsonPath("$[1].url").value(feedUrl2));
    }

    private ResultActions findRssFeed(String feedUrl) throws Exception{
        String requestUrl = String.format(
            "/api/private/v1/favorites/rss/find?url=%s",
            feedUrl);

        return mockMvc.perform(get(requestUrl).with(securityContext(studentSecurityContext()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
