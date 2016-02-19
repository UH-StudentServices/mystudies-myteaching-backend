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

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


public class UsefulLinkServiceTest extends SpringTest {

    private static final String EDU_PERSON_PRINCIPAL_NAME = "person@helsinki.fi";
    private static final String MOCK_WEBPAGE_URL = "http://www.helsinki.fi";
    private static final String MOCK_WEBPAGE_TITLE = "Helsinki University site title";

    private static final int USEFUL_LINKS_COUNT_FOR_DEFAULT_STUDENT = 6;
    private static final int USEFUL_LINKS_COUNT_FOR_STUDENT_WITH_UNKNOWN_FACULTY = 5;
    private static final int USEFUL_LINKS_COUNT_FOR_OPEN_UNI_STUDENT = 2;
    private static final int USEFUL_LINKS_COUNT_FOR_DEFAULT_TEACHER = 7;
    private static final int USEFUL_LINKS_COUNT_FOR_OPEN_UNI_TEACHER = 4;

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

        assertEquals(usefulLinkDtoList.size(), 2);
        assertEquals("Google", usefulLinkDtoList.get(0).description);
        assertEquals("http://www.google.com", usefulLinkDtoList.get(0).url);
        assertEquals("USER_DEFINED", usefulLinkDtoList.get(0).type);
        assertEquals("Helsinki University", usefulLinkDtoList.get(1).description);
        assertEquals("http://www.helsinki.fi", usefulLinkDtoList.get(1).url);
        assertEquals("USER_DEFINED", usefulLinkDtoList.get(1).type);
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
        assertTrue(usefulLinkDtoOptional.isPresent());
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

        assertEquals(usefulLinkDtoOptional.get().url, url);
        assertEquals(usefulLinkDtoOptional.get().description, description);
    }

    @Test
    public void thatUsefulLinkIsDeleted() {
        usefulLinkService.delete(1L);
        List<UsefulLinkDto> usefulLinkDtoList = usefulLinkService.findByUserId(3L, englishLocale());
        Optional<UsefulLinkDto> usefulLinkDtoOptional = usefulLinkDtoList.stream().filter(u -> u.id.equals(1L))
            .findFirst();
        assertFalse(usefulLinkDtoOptional.isPresent());
    }

    @Test
    @Transactional
    public void thatStudentDefaultUsefulLinksAreCreated() {
        configureStudentSecurityContext();

        defaultStudentRequestChain()
            .enrollments()
            .studyRights();

        List<UsefulLinkDto> usefulLinks = createDefaultLinksForUser(securityUtils.getAppUser().get());
        assertEquals(USEFUL_LINKS_COUNT_FOR_DEFAULT_STUDENT, usefulLinks.size());
        checkLinkURLAndDescription(usefulLinks.get(0),
            "https://flamma.helsinki.fi/fi/HY286484",
            "usefulLinks.forStudent");
        checkLinkURLAndDescription(usefulLinks.get(1),
            "https://opinder.helsinki.fi",
            "usefulLinks.opinder");
        checkLinkURLAndDescription(usefulLinks.get(2),
            "https://flamma.helsinki.fi/en/HY034217",
            "usefulLinks.healthAndWellbeing");
        checkLinkURLAndDescription(usefulLinks.get(3),
            "https://flamma.helsinki.fi/portal/home/sisalto?_nfpb=true&_pageLabel=pp_list&placeId=HY034234",
            "usefulLinks.studyingAndTrainingAbroad");
        checkLinkURLAndDescription(usefulLinks.get(4),
            "http://www.aarresaari.net/index.php?32&uniid=7&lang_id=1",
            "usefulLinks.rekryNet");
        checkLinkURLAndDescription(usefulLinks.get(5),
            "https://helsinkifi-my.sharepoint.com/",
            "usefulLinks.oneDrive");
    }

    @Test
    @Transactional
    public void thatTeacherDefaultUsefulLinksAreCreated() {
        configureTeacherSecurityContext();

        defaultTeacherRequestChain().courses();

        List<UsefulLinkDto> usefulLinks = createDefaultLinksForUser(securityUtils.getAppUser().get());
        assertEquals(USEFUL_LINKS_COUNT_FOR_DEFAULT_TEACHER, usefulLinks.size());

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
        assertEquals(searchResult.searchResult, MOCK_WEBPAGE_TITLE);
    }

    @Test
    public void thatUsefulLinkOrderIsUpdated() {
        OrderUsefulLinksDto orderUsefulLinksDto = new OrderUsefulLinksDto();
        orderUsefulLinksDto.usefulLinkIds = Lists.newArrayList(2L, 1L);

        List<UsefulLinkDto> usefulLinkDtos = usefulLinkService.updateOrder(3L, orderUsefulLinksDto, englishLocale());

        assertTrue(usefulLinkDtos.get(0).id.equals(2L));
        assertTrue(usefulLinkDtos.get(1).id.equals(1L));
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

        assertEquals(USEFUL_LINKS_COUNT_FOR_OPEN_UNI_STUDENT, usefulLinks.size());
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

        assertEquals(USEFUL_LINKS_COUNT_FOR_OPEN_UNI_TEACHER, usefulLinks.size());
        checkLinkURLAndDescription(usefulLinks.get(0),
            "https://flamma.helsinki.fi/fi/HY311604",
            "usefulLinks.teachingSupportOpenUniversity");
        checkLinkURLAndDescription(usefulLinks.get(1),
            "https://flamma.helsinki.fi/fi/HY311747",
            "usefulLinks.teachingPracticesInOpenUniversity");
        checkLinkURLAndDescription(usefulLinks.get(2),
            "https://www.avoin.helsinki.fi/opettajille/pedagoginen_koulutus.htm",
            "usefulLinks.pedagogicTraining");
        checkLinkURLAndDescription(usefulLinks.get(3),
            "https://www.avoin.helsinki.fi/opettajille/index.htm",
            "usefulLinks.forTeachers");
    }

    @Test
    @Transactional
    public void thatDefaultFacultyLinkIsAddedWhenStudentHasUnknownFaculty() {
        configureStudentSecurityContext();

        defaultStudentRequestChain()
            .enrollments()
            .studyRights("studentstudyrightswithunknownfaculty.json");

        List<UsefulLinkDto> usefulLinks = createDefaultLinksForUser(securityUtils.getAppUser().get());

        assertEquals(USEFUL_LINKS_COUNT_FOR_STUDENT_WITH_UNKNOWN_FACULTY, usefulLinks.size());
        checkLinkURLAndDescription(usefulLinks.get(0),
            "https://flamma.helsinki.fi/portal/home/sisalto?_nfpb=true&_pageLabel=pp_list&placeId=HY034202",
            "usefulLinks.forStudent");
    }

    private Locale englishLocale() {
        return new Locale("en");
    }

    private void checkLinkURLAndDescription(UsefulLinkDto link, String url, String description) {
        assertEquals(url, link.url);
        assertEquals(description, link.description);
    }
}
