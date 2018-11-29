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

package fi.helsinki.opintoni;

import com.google.common.collect.ImmutableMap;
import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.config.Constants;
import fi.helsinki.opintoni.integration.fileservice.FileServiceStorage;
import fi.helsinki.opintoni.integration.newsfeeds.FlammaRestClient;
import fi.helsinki.opintoni.integration.newsfeeds.GuideNewsRestClient;
import fi.helsinki.opintoni.integration.publicwww.PublicWwwRestClient;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.security.enumerated.SAMLEduPersonAffiliation;
import fi.helsinki.opintoni.server.*;
import fi.helsinki.opintoni.util.DateTimeUtil;
import fi.helsinki.opintoni.web.TestConstants;
import fi.helsinki.opintoni.web.requestchain.*;
import fi.helsinki.opintoni.web.rest.RestConstants;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.Filter;
import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.util.Lists.newArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    classes = TestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(Constants.SPRING_PROFILE_TEST)
@Transactional
public abstract class SpringTest {

    private static final List<String> TABLE_NAMES = newArrayList(
        "user_account",
        "favorite",
        "profile",
        "user_settings",
        "todo_item",
        "useful_link",
        "user_notification",
        "profile_keyword_relationship",
        "profile_keyword",
        "contact_information",
        "some_link",
        "degree",
        "calendar_feed",
        "job_search",
        "study_attainment_whitelist",
        "study_attainment_whitelist_entry",
        "work_experience",
        "component_visibility",
        "localized_text",
        "free_text_content",
        "sample",
        "office_hours",
        "degree_programme",
        "component_order",
        "component_heading",
        "office_hours_degree_programme",
        "cached_item_updates_check",
        "notifications",
        "notification_schedules",
        "profile_background",
        "profile_language_proficiency",
        "profile_shared_link",
        "teaching_language",
        "office_hours_teaching_language");
    private static final ImmutableMap<String, String> SEQUENCE_NAMES_BY_TABLE_NAMES = ImmutableMap.of("user_account", "user_id_seq");
    private static final String DEFAULT_SEQUENCE_SUFFIX = "_id_seq";

    protected OodiServer oodiServer;
    protected CoursePageServer coursePageServer;
    protected GuideServer guideServer;
    protected WebPageServer webPageServer;
    protected FlammaServer flammaServer;
    protected PublicWwwServer publicWwwServer;
    protected UnisportServer unisportServer;
    protected ESBServer esbServer;
    protected GuideNewsServer guideNewsServer;

    protected MockMvc mockMvc;

    @Autowired
    protected RestTemplate oodiRestTemplate;

    @Autowired
    protected RestTemplate coursePageRestTemplate;

    @Autowired
    protected RestTemplate guideRestTemplate;

    @Autowired
    protected FlammaRestClient flammaRestClient;

    @Autowired
    protected GuideNewsRestClient guideNewsRestClient;

    @Autowired
    protected RestTemplate esbRestTemplate;

    @Autowired
    protected PublicWwwRestClient publicWwwRestClient;

    @Autowired
    protected RestTemplate metaDataRestTemplate;

    @Autowired
    RestTemplate unisportRestTemplate;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CacheManager transientCacheManager;

    @Autowired
    private CacheManager persistentCacheManager;

    @Autowired
    private SpringLiquibase springLiquibase;

    @Autowired
    protected AppConfiguration appConfiguration;

    @Autowired
    private Environment environment;

    @Autowired
    private FileServiceStorage fileServiceStorage;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void initRestServer() {
        oodiServer = new OodiServer(appConfiguration, oodiRestTemplate);
        coursePageServer = new CoursePageServer(appConfiguration, coursePageRestTemplate);
        guideServer = new GuideServer(appConfiguration, guideRestTemplate);
        flammaServer = new FlammaServer(appConfiguration, flammaRestClient.getRestTemplate());
        guideNewsServer = new GuideNewsServer(appConfiguration,
            guideNewsRestClient.getRestTemplate());
        publicWwwServer = new PublicWwwServer(appConfiguration, publicWwwRestClient.getRestTemplate());
        webPageServer = new WebPageServer(metaDataRestTemplate);
        unisportServer = new UnisportServer(appConfiguration, unisportRestTemplate);
        esbServer = new ESBServer(appConfiguration, esbRestTemplate);
        configureMockMvc();
    }

    @Before
    public void clearDatabase() throws LiquibaseException {
        springLiquibase.afterPropertiesSet();
    }

    @Before
    public void clearCaches() {
        clearCaches(transientCacheManager);
        clearCaches(persistentCacheManager);
    }

    private void clearCaches(CacheManager cacheManager) {
        cacheManager.getCacheNames()
            .stream()
            .map(cacheManager::getCache)
            .forEach(Cache::clear);
    }

    @Before
    public void clearFileStorage() {
        fileServiceStorage.clear();
    }

    @After
    public void verify() {
        oodiServer.verify();
        coursePageServer.verify();
        guideServer.verify();
        flammaServer.verify();
        guideNewsServer.verify();
        publicWwwServer.verify();
        webPageServer.verify();
        unisportServer.verify();
        esbServer.verify();
    }

    @Before
    public void resetSequences() {
        TABLE_NAMES.stream().forEach(tableName -> {

            String sequenceName = SEQUENCE_NAMES_BY_TABLE_NAMES.get(tableName);

            if (sequenceName == null) {
                sequenceName = tableName + DEFAULT_SEQUENCE_SUFFIX;
            }

            entityManager.createNativeQuery(createSequenceResetQuery(sequenceName, tableName)).getSingleResult();
        });
    }

    private String createSequenceResetQuery(String sequenceName, String tableName) {
        return String.format("select setval('%s', (select coalesce(max(id)+1,1) from %s), false);", sequenceName, tableName);
    }

    private void configureMockMvc() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .addFilters(springSecurityFilterChain)
            .build();
    }

    protected void configureStudentSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(new TestingAuthenticationToken(
            new AppUser.AppUserBuilder()
                .studentNumber(TestConstants.STUDENT_NUMBER)
                .eduPersonPrincipalName("opiskelija@helsinki.fi")
                .eduPersonAffiliations(Arrays.asList(SAMLEduPersonAffiliation.MEMBER, SAMLEduPersonAffiliation.STUDENT))
                .eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation.STUDENT)
                .oodiPersonId("1111")
                .build(),
            ""));
    }

    protected void configureTeacherSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(new TestingAuthenticationToken(
            new AppUser.AppUserBuilder()
                .employeeNumber(TestConstants.EMPLOYEE_NUMBER)
                .eduPersonPrincipalName("opettaja@helsinki.fi")
                .eduPersonAffiliations(singletonList(SAMLEduPersonAffiliation.FACULTY))
                .eduPersonPrimaryAffiliation(SAMLEduPersonAffiliation.FACULTY)
                .teacherFacultyCode("A10000")
                .oodiPersonId("2222")
                .build(),
            ""));
    }

    protected TeacherRequestChain defaultTeacherRequestChain() {
        return new TeacherRequestChain(TestConstants.EMPLOYEE_NUMBER,
            DateTimeUtil.getSemesterStartDateString(LocalDate.now()),
            oodiServer,
            coursePageServer);
    }

    protected TeacherRequestChain teacherRequestChain(String teacherNumber) {
        return new TeacherRequestChain(teacherNumber, DateTimeUtil.getSemesterStartDateString(LocalDate.now()), oodiServer, coursePageServer);
    }

    protected StudentRequestChain defaultStudentRequestChain() {
        return new StudentRequestChain(TestConstants.STUDENT_NUMBER, oodiServer, coursePageServer);
    }

    protected StudentRequestChain studentRequestChain(String studentNumber) {
        return new StudentRequestChain(studentNumber, oodiServer, coursePageServer);
    }

    protected OodiCourseNamesRequestChain defaultOodiCourseNamesRequestChain() {
        return new OodiCourseNamesRequestChain(oodiServer);
    }

    protected String getRemoteMockApiUrl(String path) {
        return String.format("http://%s:%s%s/%s",
            environment.getProperty("server.address"),
            environment.getProperty("local.server.port"),
            RestConstants.PUBLIC_API_V1,
            path);
    }

    protected String getMockFeedApiUrl() {
        return getRemoteMockApiUrl(String.format("mockfeed?id=%s", "1"));
    }

    protected String getMockFeedApiUrl(String feedId) {
        return getRemoteMockApiUrl(String.format("mockfeed?id=%s", feedId));
    }

    protected Cookie langCookie(Language language) {
        return new Cookie(Constants.NG_TRANSLATE_LANG_KEY, language.getCode());
    }

}
