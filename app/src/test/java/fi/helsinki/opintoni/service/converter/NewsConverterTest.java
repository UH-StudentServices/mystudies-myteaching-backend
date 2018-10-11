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

package fi.helsinki.opintoni.service.converter;

import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.FeedException;
import fi.helsinki.opintoni.dto.NewsDto;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NewsConverterTest {

    private static Feed feed;

    private NewsConverter testClass;

    @BeforeClass
    public static void init() throws IOException, FeedException, ParseException {

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("atom_0.3");
        feed.setTitle("test-title");
        feed.setDescription("test-description");
        feed.setLink("https://example.org");

        List<SyndEntry> entries = new ArrayList<>();
        entries.add(createEntry("title", "<p>Jes <em>tärkeä</em></p>", "2004-06-08", "http://example.com/1"));
        entries.add(createEntry("title", "<p>Jes <em>&auml;&auml;kk&ouml;nen</em></p>", "2004-06-08", "http://example.com/1"));

        feed.setEntries(entries);

        NewsConverterTest.feed = (Feed) feed.createWireFeed();

    }

    @Before
    public void initTest() {
        testClass = new NewsConverter();
    }

    @Test
    public void thatTagsAreStripped() {
        NewsDto newsDto = testClass.toDtoFromAtom(feed.getEntries().get(0));
        Assertions.assertThat(newsDto.content).isEqualTo("Jes tärkeä");
    }

    @Test
    public void thatEntitiesAreUnescaped() {
        NewsDto newsDto = testClass.toDtoFromAtom(feed.getEntries().get(1));
        Assertions.assertThat(newsDto.content).isEqualTo("Jes ääkkönen");
    }

    private static SyndEntry createEntry(String title, String description, String updated, String url)
        throws ParseException {

        final DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");

        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(title);
        entry.setUpdatedDate(DATE_PARSER.parse(updated));
        entry.setLink(url);

        SyndContent desc = new SyndContentImpl();
        desc.setType("text/html");
        desc.setValue(description);
        entry.setDescription(desc);

        return entry;
    }

}
