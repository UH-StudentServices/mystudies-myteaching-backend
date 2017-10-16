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
import fi.helsinki.opintoni.dto.NewsDto;
import fi.helsinki.opintoni.service.news.FlammaNewsService;
import fi.helsinki.opintoni.service.news.NewsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class NewsServiceCacheTest extends SpringTest {

    @Autowired
    private FlammaNewsService flammaNewsService;

    @Test
    public void thatStudentNewsAreCached() {
        flammaServer.expectStudentNews();
        guideNewsServer.expectGuideNewsFi();

        List<NewsDto> news = flammaNewsService.getStudentNews(new Locale("fi"));
        List<NewsDto> cachedNews = flammaNewsService.getStudentNews(new Locale("fi"));

        assertThat(cachedNews).isSameAs(news);
    }

    @Test
    public void thatTeacherNewsAreCached() {
        flammaServer.expectTeacherNews();

        List<NewsDto> news = flammaNewsService.getTeacherNews(new Locale("fi"));
        List<NewsDto> cachedNews = flammaNewsService.getTeacherNews(new Locale("fi"));

        assertThat(cachedNews).isSameAs(news);
    }
}
