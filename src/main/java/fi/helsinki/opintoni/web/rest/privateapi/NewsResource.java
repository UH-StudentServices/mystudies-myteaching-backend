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

import fi.helsinki.opintoni.dto.NewsDto;
import fi.helsinki.opintoni.security.authorization.StudentRoleRequired;
import fi.helsinki.opintoni.security.authorization.TeacherRoleRequired;
import fi.helsinki.opintoni.service.news.NewsService;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_API_V1 + "/news",
    produces = WebConstants.APPLICATION_JSON_UTF8)
public class NewsResource extends AbstractResource {

    private final NewsService newsService;

    @Autowired
    public NewsResource(NewsService newsService) {
        this.newsService = newsService;
    }

    @StudentRoleRequired
    @RequestMapping(value = "/student")
    public ResponseEntity<List<NewsDto>> getStudentNews(Locale locale) {
        return response(newsService.getStudentNews(locale));
    }

    @TeacherRoleRequired
    @RequestMapping(value = "/teacher")
    public ResponseEntity<List<NewsDto>> getTeacherNews(Locale locale) {
        return response(newsService.getTeacherNews(locale));
    }

    @RequestMapping(value = "/openuniversity")
    public ResponseEntity<List<NewsDto>> getOpenUniversityNews() {
        return response(newsService.getOpenUniversityNews());
    }
}
