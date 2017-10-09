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
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "newsfeeds")
public class FlammaRestClient extends AtomRestClient{
    private final static Logger log = LoggerFactory.getLogger(FlammaRestClient.class);

    private Map<String, String> studentFeedsByLocale;
    private Map<String, String> teacherFeedsByLocale;

    public Feed getStudentFeed(Locale locale) {
        return getFeed(studentFeedsByLocale.get(locale.getLanguage()));
    }

    public Feed getTeacherFeed(Locale locale) {
        return getFeed(teacherFeedsByLocale.get(locale.getLanguage()));
    }

    public Map<String, String> getStudentFeedsByLocale() {
        return studentFeedsByLocale;
    }

    public void setStudentFeedsByLocale(
        Map<String, String> studentFeedsByLocale) {
        this.studentFeedsByLocale = studentFeedsByLocale;
    }

    public Map<String, String> getTeacherFeedsByLocale() {
        return teacherFeedsByLocale;
    }

    public void setTeacherFeedsByLocale(
        Map<String, String> teacherFeedsByLocale) {
        this.teacherFeedsByLocale = teacherFeedsByLocale;
    }

}
