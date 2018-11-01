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

import com.google.common.collect.ImmutableMap;
import com.rometools.rome.feed.atom.Feed;
import java.util.List;
import java.util.Locale;

public class GuideNewsMockClient extends NewsFeedsMockClient implements GuideNewsClient {

    private static final String MOCK_DATA_BASE = "/sampledata/newsfeeds/guide/";

    private static final ImmutableMap<String, String> LANG_FILES = ImmutableMap.of(
        "fi", "feed.xml",
        "sv", "feed-sv.xml",
        "en", "feed-en.xml");

    @Override
    public Feed getGuideFeed(Locale locale) {
        return getFeedFromPath(MOCK_DATA_BASE + LANG_FILES.get(locale.getLanguage()));
    }

    @Override
    public Feed getGuideFeed(Locale locale, List<String> degreeProgrammeOrMajorCodes) {
        // no proper mocking for degree programme news yet, should be relatively straightforward
        // to add if needed
        return getGuideFeed(locale);
    }
}
