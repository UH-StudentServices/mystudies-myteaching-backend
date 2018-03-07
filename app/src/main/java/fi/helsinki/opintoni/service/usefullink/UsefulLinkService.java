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

package fi.helsinki.opintoni.service.usefullink;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.dto.OrderUsefulLinksDto;
import fi.helsinki.opintoni.dto.SearchPageTitleDto;
import fi.helsinki.opintoni.dto.UsefulLinkDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.security.AppUser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Locale;

@Service
public class UsefulLinkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsefulLinkService.class);

    private final RestTemplate linkUrlLoaderRestTemplate;
    private final UsefulLinkTransactionalService usefulLinkTransactionalService;
    private final StudentDefaultUsefulLinksService studentDefaultUsefulLinksService;
    private final TeacherDefaultUsefulLinksService teacherDefaultUsefulLinksService;

    @Autowired
    public UsefulLinkService(RestTemplate linkUrlLoaderRestTemplate,
                             UsefulLinkTransactionalService usefulLinkTransactionalService,
                             StudentDefaultUsefulLinksService studentDefaultUsefulLinksService,
                             TeacherDefaultUsefulLinksService teacherDefaultUsefulLinksService) {
        this.usefulLinkTransactionalService = usefulLinkTransactionalService;
        this.studentDefaultUsefulLinksService = studentDefaultUsefulLinksService;
        this.teacherDefaultUsefulLinksService = teacherDefaultUsefulLinksService;
        this.linkUrlLoaderRestTemplate = linkUrlLoaderRestTemplate;
    }

    public UsefulLinkDto insert(final Long userId, final UsefulLinkDto usefulLinkDto, Locale locale) {
        return usefulLinkTransactionalService.insert(userId, usefulLinkDto, locale);
    }

    public void delete(final Long usefulLinkId) {
        usefulLinkTransactionalService.delete(usefulLinkId);
    }

    public List<UsefulLinkDto> findByUserId(Long userId, Locale locale) {
        return usefulLinkTransactionalService.findByUserId(userId, locale);
    }

    public UsefulLinkDto update(Long usefulLinkId, UsefulLinkDto usefulLinkDto, Locale locale) {
        return usefulLinkTransactionalService.update(usefulLinkId, usefulLinkDto, locale);
    }

    public List<UsefulLinkDto> updateOrder(Long userId, OrderUsefulLinksDto orderUsefulLinksDto, Locale locale) {
        return usefulLinkTransactionalService.updateOrder(userId, orderUsefulLinksDto, locale);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void createUserDefaultUsefulLinks(User user, AppUser appUser) {
        if (appUser.isTeacher()) {
            teacherDefaultUsefulLinksService.createDefaultLinks(user, appUser);
        } else {
            studentDefaultUsefulLinksService.createDefaultLinks(user, appUser);
        }
    }

    public SearchPageTitleDto searchPageTitle(SearchPageTitleDto searchPageTitleDto) throws NotFoundException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Lists.newArrayList(MediaType.TEXT_HTML));
            headers.add("User-Agent", "Mozilla");
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> responseEntity = linkUrlLoaderRestTemplate.exchange(searchPageTitleDto.searchUrl,
                HttpMethod.GET, entity, String.class);
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                Document document = Jsoup.parse(responseEntity.getBody());
                searchPageTitleDto.searchResult = document.title();
            }
        } catch (Exception e) {
            LOGGER.error("Error when searching gor page title with url: {}: {}", searchPageTitleDto.searchUrl, e.getMessage());
        }
        return searchPageTitleDto;
    }
}
