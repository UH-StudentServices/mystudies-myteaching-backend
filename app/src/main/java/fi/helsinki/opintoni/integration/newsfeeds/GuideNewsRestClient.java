package fi.helsinki.opintoni.integration.newsfeeds;

import com.rometools.rome.feed.atom.Feed;
import java.util.Locale;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "newsfeeds")
public class GuideNewsRestClient extends AtomRestClient {

    private Map<String, String> guideFeedsByLocale;

    public Feed getGuideFeed(Locale locale) {
        return getFeed(guideFeedsByLocale.get(locale.getLanguage()));
    }

    public Feed getGuideFeed(Locale locale, String degreeProgrammeCode) {
        String uri = guideFeedsByLocale.get(locale.getLanguage()) + "?degree_programme_code=" + degreeProgrammeCode;
        return getFeed(uri);
    }

    public Map<String, String> getGuideFeedsByLocale() {
        return guideFeedsByLocale;
    }

    public void setGuideFeedsByLocale(
        Map<String, String> guideFeedsByLocale) {
        this.guideFeedsByLocale = guideFeedsByLocale;
    }
}
