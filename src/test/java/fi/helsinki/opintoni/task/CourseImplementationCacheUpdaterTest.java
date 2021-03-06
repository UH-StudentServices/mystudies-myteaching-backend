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

package fi.helsinki.opintoni.task;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.CachedItemUpdatesCheck;
import fi.helsinki.opintoni.integration.IntegrationUtil;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageIntegrationException;
import fi.helsinki.opintoni.repository.CachedItemUpdatesCheckRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static fi.helsinki.opintoni.cache.CacheConstants.COURSE_PAGE;
import static fi.helsinki.opintoni.web.TestConstants.TEACHER_COURSE_REALISATION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CourseImplementationCacheUpdaterTest extends SpringTest {
    private static final String COURSE_IMPLEMENTATION_RESPONSE = "teacher_course.json";
    private static final String UPDATED_COURSE_IMPLEMENTATION_RESPONSE = "updated_teacher_course.json";
    private static final String UPDATED_COURSE_IMPLEMENTATIONS_RESPONSE = "updated_teacher_courses.json";
    private static final String EMPTY_COURSE_IMPLEMENTATION_RESPONSE = "course_empty.json";
    private static final String UPDATED_COURSE_TITLE = "Updated title";
    private static final String UPDATED_COURSE_TITLE_2 = "Updated title 2";
    private static final String SECOND_COURSE_REALISATION_ID = "99903630";
    private static final String STRIPPED_TEACHER_CUR_ID = IntegrationUtil.stripPossibleSisuOodiCurPrefix(TEACHER_COURSE_REALISATION_ID);

    private static final String UPDATED_COURSE_REALISATION_IDS = String.join(",", STRIPPED_TEACHER_CUR_ID, SECOND_COURSE_REALISATION_ID);

    @Autowired
    private CoursePageClient coursePageRestClient;

    @Autowired
    private CourseImplementationCacheUpdater courseImplementationUpdatesChecker;

    @Autowired
    private CacheManager persistentCacheManager;

    @Autowired
    private Environment environment;

    @Autowired
    private CachedItemUpdatesCheckRepository cachedItemUpdatesCheckRepository;

    private CoursePageCourseImplementation getCourseImplementationFromCache(String implementationId, Locale locale) {
        Cache.ValueWrapper w = persistentCacheManager.getCache(COURSE_PAGE)
            .get(String.format("%s_%s", implementationId, locale.toString()));
        return w == null ? null : (CoursePageCourseImplementation) w.get();
    }

    @Before
    public void init() {
        CachedItemUpdatesCheck cachedItemUpdatesCheck = new CachedItemUpdatesCheck();
        cachedItemUpdatesCheck.cacheName = COURSE_PAGE;
        cachedItemUpdatesCheck.lastChecked = LocalDateTime.now();
        cachedItemUpdatesCheckRepository.save(cachedItemUpdatesCheck);
    }

    private LocalDateTime getLastCheckDateTime() {
        return cachedItemUpdatesCheckRepository
            .findByCacheName(COURSE_PAGE)
            .orElseThrow(() -> new RuntimeException("No course page update checks found")).lastChecked;
    }

    private List<Locale> getAvailableLocales() {
        List<String> availableLanguages = environment.getRequiredProperty("language.available", List.class);

        return availableLanguages.stream().map(Locale::new).collect(Collectors.toList());
    }

    private void assertGetCourseImplementationAndUpdateCachedItem(Locale locale) {
        coursePageServer.expectCourseImplementationRequest(STRIPPED_TEACHER_CUR_ID, COURSE_IMPLEMENTATION_RESPONSE, locale);
        coursePageServer.expectCourseImplementationRequest(SECOND_COURSE_REALISATION_ID, COURSE_IMPLEMENTATION_RESPONSE, locale);
        LocalDateTime initialLastCheckDateTime = getLastCheckDateTime();
        coursePageServer.expectCourseImplementationChangesRequestWhenMultipleChanges(initialLastCheckDateTime);

        List<Locale> availableLocales = getAvailableLocales();

        availableLocales.forEach(availableLocale -> {
                coursePageServer.expectCourseImplementationRequest(STRIPPED_TEACHER_CUR_ID + "," + SECOND_COURSE_REALISATION_ID,
                        UPDATED_COURSE_IMPLEMENTATIONS_RESPONSE, availableLocale);
            }
        );

        CoursePageCourseImplementation courseImplementation = coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, locale);

        CoursePageCourseImplementation courseImplementationFromCache = getCourseImplementationFromCache(TEACHER_COURSE_REALISATION_ID, locale);

        assertThat(courseImplementation).isEqualToComparingFieldByFieldRecursively(courseImplementationFromCache);

        courseImplementationFromCache = getCourseImplementationFromCache(SECOND_COURSE_REALISATION_ID, locale);
        assertThat(courseImplementationFromCache).isNull();

        courseImplementation = coursePageRestClient.getCoursePage(SECOND_COURSE_REALISATION_ID, locale);

        courseImplementationFromCache = getCourseImplementationFromCache(SECOND_COURSE_REALISATION_ID, locale);

        assertThat(courseImplementation).isEqualToComparingFieldByFieldRecursively(courseImplementationFromCache);

        courseImplementationUpdatesChecker.getCourseImplementationChangesAndUpdateCache();

        availableLocales.forEach(availableLocale -> {
            CoursePageCourseImplementation implementationAfterUpdate =
                getCourseImplementationFromCache(STRIPPED_TEACHER_CUR_ID, availableLocale);
            assertThat(implementationAfterUpdate.title).isEqualTo(UPDATED_COURSE_TITLE);

            implementationAfterUpdate =
                    getCourseImplementationFromCache(SECOND_COURSE_REALISATION_ID, availableLocale);
            assertThat(implementationAfterUpdate.title).isEqualTo(UPDATED_COURSE_TITLE_2);

        });

        assertThat(initialLastCheckDateTime).isBefore(getLastCheckDateTime());
    }

    @Test
    public void thatNewCoursesAreInsertedToCache() {
        List<Locale> availableLocales = getAvailableLocales();

        coursePageServer.expectCourseImplementationChangesRequestWhenMultipleChanges(getLastCheckDateTime());

        availableLocales.forEach(availableLocale ->
            coursePageServer.expectCourseImplementationRequest(
                UPDATED_COURSE_REALISATION_IDS,
                UPDATED_COURSE_IMPLEMENTATIONS_RESPONSE, availableLocale));

        courseImplementationUpdatesChecker.getCourseImplementationChangesAndUpdateCache();

        availableLocales.stream().forEach(l -> {
            assertThat(getCourseImplementationFromCache(STRIPPED_TEACHER_CUR_ID, l)).isNotNull();
            assertThat(getCourseImplementationFromCache(SECOND_COURSE_REALISATION_ID, l)).isNotNull();
        });
    }

    @Test
    public void thatCourseImplementationIsCachedInFinnishAndUpdated() {
        assertGetCourseImplementationAndUpdateCachedItem(new Locale("fi"));
    }

    @Test
    public void thatCourseImplementationIsCachedInEnglishAndUpdated() {
        assertGetCourseImplementationAndUpdateCachedItem(Locale.ENGLISH);
    }

    @Test
    public void thatCourseImplementationIsCachedInSwedishAndUpdated() {
        assertGetCourseImplementationAndUpdateCachedItem(new Locale("sv"));
    }

    @Test
    public void thatCachedItemIsRetainedIfCourseImplementationHasNotBeenUpdated() {
        Locale locale = Locale.ENGLISH;

        coursePageServer.expectCourseImplementationRequest(STRIPPED_TEACHER_CUR_ID, COURSE_IMPLEMENTATION_RESPONSE, locale);

        coursePageServer.expectCourseImplementationChangesRequestWhenNoChanges(getLastCheckDateTime());

        CoursePageCourseImplementation courseImplementation = coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, locale);

        courseImplementationUpdatesChecker.getCourseImplementationChangesAndUpdateCache();

        CoursePageCourseImplementation courseImplementationFromCache = getCourseImplementationFromCache(TEACHER_COURSE_REALISATION_ID, locale);

        assertThat(courseImplementation).isEqualToComparingFieldByFieldRecursively(courseImplementationFromCache);
    }

    @Test
    public void thatLastCheckDateTimeIsNotIncrementedIfCourseImplementationChangesRequestFails() {
        LocalDateTime initialLastCheckDateTime = getLastCheckDateTime();

        coursePageServer.expectCourseImplementationChangesRequestToRespondError(getLastCheckDateTime());

        assertThatExceptionOfType(CoursePageIntegrationException.class).isThrownBy(() ->
            courseImplementationUpdatesChecker.getCourseImplementationChangesAndUpdateCache());

        assertThat(initialLastCheckDateTime).isEqualTo(getLastCheckDateTime());
    }

    @Test
    public void thatDummyCourseImplementationIsNotCached() {
        String nonExistingId = "does-not-exist";
        Locale locale = Locale.ENGLISH;
        coursePageServer.expectCourseImplementationRequestAndReturnStatus(nonExistingId, locale, HttpStatus.NOT_FOUND);

        CoursePageCourseImplementation dummy = coursePageRestClient.getCoursePage(nonExistingId, locale);
        assertThat(getCourseImplementationFromCache(nonExistingId, locale)).isNull();
    }

    @Test
    public void thatEmptyCourseImplementationIsCached() {
        Locale locale = Locale.ENGLISH;
        coursePageServer.expectCourseImplementationRequest(STRIPPED_TEACHER_CUR_ID, EMPTY_COURSE_IMPLEMENTATION_RESPONSE, locale);

        CoursePageCourseImplementation courseImplementation = coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, locale);

        CoursePageCourseImplementation courseImplementationFromCache = getCourseImplementationFromCache(TEACHER_COURSE_REALISATION_ID, locale);

        assertThat(courseImplementation).isEqualToComparingFieldByFieldRecursively(courseImplementationFromCache);
    }

    @Test
    public void thatLastCheckDateTimeIsNotIncrementedIfNotAllRequestedCourseDataIsReturned() {
        LocalDateTime initialLastCheckDateTime = getLastCheckDateTime();
        coursePageServer.expectCourseImplementationChangesRequestWhenMultipleChanges(initialLastCheckDateTime);

        coursePageServer.expectCourseImplementationRequest(STRIPPED_TEACHER_CUR_ID + "," + SECOND_COURSE_REALISATION_ID,
                UPDATED_COURSE_IMPLEMENTATION_RESPONSE, getAvailableLocales().get(0));

        assertThatExceptionOfType(CoursePageIntegrationException.class).isThrownBy(() ->
                courseImplementationUpdatesChecker.getCourseImplementationChangesAndUpdateCache());

        assertThat(initialLastCheckDateTime).isEqualTo(getLastCheckDateTime());
    }
}
