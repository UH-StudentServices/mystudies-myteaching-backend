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

import java.util.Locale;
import java.util.Map;

public class FlammaRestClient extends AtomRestClient implements FlammaClient {

    private Map<String, String> studentFeedsByLocale;
    private Map<String, String> teacherFeedsByLocale;

    public FlammaRestClient(
        RestTemplate restTemplate, Map<String, String> studentFeedsByLocale, Map<String, String> teacherFeedsByLocale) {
        super(restTemplate);
        this.studentFeedsByLocale = studentFeedsByLocale;
        this.teacherFeedsByLocale = teacherFeedsByLocale;
    }

    @Override
    public Feed getStudentFeed(Locale locale) {
        return getFeed(studentFeedsByLocale.get(locale.getLanguage()));
    }

    @Override
    public Feed getTeacherFeed(Locale locale) {
        return getFeed(teacherFeedsByLocale.get(locale.getLanguage()));
    }

}
