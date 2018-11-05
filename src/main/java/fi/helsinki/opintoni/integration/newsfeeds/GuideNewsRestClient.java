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

package fi.helsinki.opintoni.integration.newsfeeds;

import com.rometools.rome.feed.atom.Feed;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class GuideNewsRestClient extends AtomRestClient implements GuideNewsClient {

    private static final String CODE_PARAM_NAME = "degree_programme_codes[]";
    private Map<String, String> guideFeedsByLocale;

    public GuideNewsRestClient(RestTemplate restTemplate, Map<String, String> guideFeedsByLocale) {
        super(restTemplate);
        this.guideFeedsByLocale = guideFeedsByLocale;
    }

    @Override
    public Feed getGuideFeed(Locale locale) {
        return getFeed(guideFeedsByLocale.get(locale.getLanguage()));
    }

    @Override
    public Feed getGuideFeed(Locale locale, List<String> degreeProgrammeOrMajorCodes) {
        String uri = guideFeedsByLocale.get(locale.getLanguage()) + "?" + getQueryParams(degreeProgrammeOrMajorCodes);
        return getFeed(uri);
    }

    private String getQueryParams(List<String> degreeProgrammeOrMajorCodes) {
        return degreeProgrammeOrMajorCodes.stream()
                .map(c -> CODE_PARAM_NAME + "=" + c)
                .collect(Collectors.joining("&"));
    }
}
