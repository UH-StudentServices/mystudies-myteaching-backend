package fi.helsinki.opintoni.service.converter;


import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import fi.helsinki.opintoni.dto.NewsDto;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

        List entries = new ArrayList();
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
