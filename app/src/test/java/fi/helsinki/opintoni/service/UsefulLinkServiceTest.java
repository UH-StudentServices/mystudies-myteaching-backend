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

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.dto.OrderUsefulLinksDto;
import fi.helsinki.opintoni.dto.SearchPageTitleDto;
import fi.helsinki.opintoni.dto.UsefulLinkDto;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.security.SecurityUtils;
import fi.helsinki.opintoni.service.usefullink.UsefulLinkService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


public class UsefulLinkServiceTest extends SpringTest {

    private static final String EDU_PERSON_PRINCIPAL_NAME = "person@helsinki.fi";
    private static final String MOCK_WEBPAGE_URL = "http://www.helsinki.fi";
    private static final String MOCK_WEBPAGE_TITLE = "Helsinki University site title";

    private static final int USEFUL_LINKS_COUNT_FOR_DEFAULT_STUDENT = 2;
    private static final int USEFUL_LINKS_COUNT_FOR_OPEN_UNI_STUDENT = 2;
    private static final int USEFUL_LINKS_COUNT_FOR_DEFAULT_TEACHER = 8;
    private static final int USEFUL_LINKS_COUNT_FOR_OPEN_UNI_TEACHER = 3;

    @Autowired
    RestTemplate linkUrlLoaderRestTemplate;

    @Autowired
    UsefulLinkService usefulLinkService;

    @Autowired
    UserService userService;

    @Autowired
    SecurityUtils securityUtils;

    @Test
    public void thatUsefulLinksAreFetchedByUserId() {
        List<UsefulLinkDto> usefulLinkDtoList = usefulLinkService.findByUserId(3L, englishLocale());

        assertThat(2).isEqualTo(usefulLinkDtoList.size());
        assertThat(usefulLinkDtoList.get(0).description).isEqualTo("Google");
        assertThat(usefulLinkDtoList.get(0).url).isEqualTo("http://www.google.com");
        assertThat(usefulLinkDtoList.get(0).type).isEqualTo("USER_DEFINED");
        assertThat(usefulLinkDtoList.get(1).description).isEqualTo("Helsinki University");
        assertThat(usefulLinkDtoList.get(1).url).isEqualTo("http://www.helsinki.fi");
        assertThat(usefulLinkDtoList.get(1).type).isEqualTo("USER_DEFINED");
    }

    @Test
    public void thatUsefulLinkIsInserted() {

        String url = "http://www.wikipedia.com";
        String description = "Wikipedia";

        UsefulLinkDto usefulLinkDto = new UsefulLinkDto();
        usefulLinkDto.url = url;
        usefulLinkDto.description = description;

        usefulLinkService.insert(3L, usefulLinkDto, englishLocale());

        List<UsefulLinkDto> usefulLinkDtoList = usefulLinkService.findByUserId(3L, englishLocale());
        Optional<UsefulLinkDto> usefulLinkDtoOptional = usefulLinkDtoList.stream().filter(u -> u.url.equals(url))
            .findFirst();
        assertThat(usefulLinkDtoOptional.isPresent()).isTrue();
    }

    @Test
    public void thatUsefulLinkIsUpdated() {
        String url = "http://www.wikipedia.com";
        String description = "Wikipedia";

        UsefulLinkDto usefulLinkDto = new UsefulLinkDto();
        usefulLinkDto.url = url;
        usefulLinkDto.description = description;

        usefulLinkService.update(1L, usefulLinkDto, englishLocale());

        List<UsefulLinkDto> usefulLinkDtoList = usefulLinkService.findByUserId(3L, englishLocale());

        Optional<UsefulLinkDto> usefulLinkDtoOptional = usefulLinkDtoList.stream().filter(u -> u.id.equals(1L))
            .findFirst();

        assertThat(url).isEqualTo(usefulLinkDtoOptional.get().url);
        assertThat(description).isEqualTo(usefulLinkDtoOptional.get().description);
    }

    @Test
    public void thatUsefulLinkIsDeleted() {
        usefulLinkService.delete(1L);
        List<UsefulLinkDto> usefulLinkDtoList = usefulLinkService.findByUserId(3L, englishLocale());
        Optional<UsefulLinkDto> usefulLinkDtoOptional = usefulLinkDtoList.stream().filter(u -> u.id.equals(1L))
            .findFirst();
        assertThat(usefulLinkDtoOptional.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void thatStudentDefaultUsefulLinksAreCreated() {
        configureStudentSecurityContext();

        defaultStudentRequestChain()
            .enrollments()
            .studyRights();

        List<UsefulLinkDto> usefulLinks = createDefaultLinksForUser(securityUtils.getAppUser().get());
        assertThat(usefulLinks.size()).isEqualTo(USEFUL_LINKS_COUNT_FOR_DEFAULT_STUDENT);
        checkLinkURLAndDescription(usefulLinks.get(0),
            "http://helsinki.fi/office365",
            "usefulLinks.email");
        checkLinkURLAndDescription(usefulLinks.get(1),
            "https://helsinkifi-my.sharepoint.com/",
            "usefulLinks.oneDrive");
    }

    @Test
    @Transactional
    public void thatTeacherDefaultUsefulLinksAreCreated() {
        configureTeacherSecurityContext();

        defaultTeacherRequestChain().courses();

        List<UsefulLinkDto> usefulLinks = createDefaultLinksForUser(securityUtils.getAppUser().get());
        assertThat(usefulLinks.size()).isEqualTo(USEFUL_LINKS_COUNT_FOR_DEFAULT_TEACHER);

        checkLinkURLAndDescription(usefulLinks.get(0),
            "https://flamma.helsinki.fi/portal/home/sisalto?_nfpb=true&_pageLabel=pp_list&placeId=HY1001598",
            "usefulLinks.academicAdministration");
        checkLinkURLAndDescription(usefulLinks.get(1),
            "https://flamma.helsinki.fi/fi/HY292308",
            "usefulLinks.teachingSupport");
        checkLinkURLAndDescription(usefulLinks.get(2),
            "https://flamma.helsinki.fi/portal/home/sisalto?_nfpb=true&_pageLabel=pp_list&placeId=HY1001593",
            "usefulLinks.professionalDevelopment");
        checkLinkURLAndDescription(usefulLinks.get(3),
            "https://flamma.helsinki.fi/en/HY053909",
            "usefulLinks.planningSupport");
        checkLinkURLAndDescription(usefulLinks.get(4),
            "http://tuhat.helsinki.fi/",
            "usefulLinks.tuhat");
        checkLinkURLAndDescription(usefulLinks.get(5),
            "https://www.researchgate.net/",
            "usefulLinks.researchGate");
        checkLinkURLAndDescription(usefulLinks.get(6),
            "http://www.helsinki.fi/optime",
            "usefulLinks.optimePortal");
        checkLinkURLAndDescription(usefulLinks.get(7),
            "https://helsinkifi-my.sharepoint.com/",
            "usefulLinks.oneDrive");
    }

    @Test
    public void thatSiteTitleGetsExtractedFromUrl() {
        MockRestServiceServer server = MockRestServiceServer.createServer(linkUrlLoaderRestTemplate);

        server
            .expect(requestTo(MOCK_WEBPAGE_URL))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("<html><head><title>" + MOCK_WEBPAGE_TITLE +
                "</title><body>content</body></html>", MediaType.TEXT_HTML));

        SearchPageTitleDto searchPageTitleDto = new SearchPageTitleDto();
        searchPageTitleDto.searchUrl = MOCK_WEBPAGE_URL;

        SearchPageTitleDto searchResult = usefulLinkService.searchPageTitle(searchPageTitleDto);
        assertThat(MOCK_WEBPAGE_TITLE).isEqualTo(searchResult.searchResult);
    }

    @Test
    public void thatUsefulLinkOrderIsUpdated() {
        OrderUsefulLinksDto orderUsefulLinksDto = new OrderUsefulLinksDto();
        orderUsefulLinksDto.usefulLinkIds = Lists.newArrayList(2L, 1L);

        List<UsefulLinkDto> usefulLinkDtos = usefulLinkService.updateOrder(3L, orderUsefulLinksDto, englishLocale());

        assertThat(usefulLinkDtos.get(0).id.equals(2L)).isTrue();
        assertThat(usefulLinkDtos.get(1).id.equals(1L)).isTrue();
    }

    private List<UsefulLinkDto> createDefaultLinksForUser(AppUser appUser) {
        Optional<User> userOptional = userService.findFirstByEduPersonPrincipalName(EDU_PERSON_PRINCIPAL_NAME);
        usefulLinkService.createUserDefaultUsefulLinks(userOptional.get(), appUser);
        return usefulLinkService.findByUserId(userOptional.get().id, englishLocale());
    }

    @Test
    @Transactional
    public void thatOpenUniversityLinksAreAddedForStudent() {
        configureStudentSecurityContext();

        defaultStudentRequestChain().enrollments("enrollmentsopenuniversity.json");

        List<UsefulLinkDto> usefulLinks = createDefaultLinksForUser(securityUtils.getAppUser().get());

        assertThat(usefulLinks.size()).isEqualTo(USEFUL_LINKS_COUNT_FOR_OPEN_UNI_STUDENT);
        checkLinkURLAndDescription(usefulLinks.get(0),
            "https://www.helsinki.fi/en/open-university",
            "usefulLinks.openUniversity");
        checkLinkURLAndDescription(usefulLinks.get(1),
            "https://www.avoin.helsinki.fi/omat/osallistumiset/",
            "usefulLinks.openUniversityMyPages");
    }

    @Test
    @Transactional
    public void thatOpenUniversityLinkIsAddedForTeacher() {
        configureTeacherSecurityContext();

        defaultTeacherRequestChain().courses("teachercoursesopenuniversity.json");

        List<UsefulLinkDto> usefulLinks = createDefaultLinksForUser(securityUtils.getAppUser().get());

        assertThat(usefulLinks.size()).isEqualTo(USEFUL_LINKS_COUNT_FOR_OPEN_UNI_TEACHER);
        checkLinkURLAndDescription(usefulLinks.get(0),
            "https://flamma.helsinki.fi/fi/HY311604",
            "usefulLinks.teachingSupportOpenUniversity");
        checkLinkURLAndDescription(usefulLinks.get(1),
            "https://flamma.helsinki.fi/fi/HY311747",
            "usefulLinks.teachingPracticesInOpenUniversity");
        checkLinkURLAndDescription(usefulLinks.get(2),
            "http://www.helsinki.fi/optime",
            "usefulLinks.optimePortal");
    }

    private Locale englishLocale() {
        return new Locale("en");
    }

    private void checkLinkURLAndDescription(UsefulLinkDto link, String url, String description) {
        assertThat(link.url).isEqualTo(url);
        assertThat(link.description).isEqualTo(description);
    }
}
