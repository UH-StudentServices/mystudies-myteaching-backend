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
import java.util.Locale;
import java.util.Map;

public class FlammaMockClient extends NewsFeedsMockClient implements FlammaClient {

    private static final String MOCK_DATA_BASE = "/sampledata/newsfeeds/flamma/";

    private static final ImmutableMap<String, String> STUDENT_FILES = ImmutableMap.of(
        "fi", "atom-news.xml",
        "sv", "atom-news-sv.xml",
        "en", "atom-news-en.xml");

    private static final ImmutableMap<String, String> TEACHER_FILES = ImmutableMap.of(
        "fi", "atom-tiedotteet-opetusasiat.xml",
        "sv", "atom-tiedotteet-opetusasiat-sv.xml",
        "en", "atom-tiedotteet-opetusasiat-en.xml");

    @Override
    public Feed getStudentFeed(Locale locale) {
        return getFeedFromPath(MOCK_DATA_BASE + STUDENT_FILES.get(locale.getLanguage()));
    }

    @Override
    public Feed getTeacherFeed(Locale locale) {
        return getFeedFromPath(MOCK_DATA_BASE + TEACHER_FILES.get(locale.getLanguage()));
    }

}
