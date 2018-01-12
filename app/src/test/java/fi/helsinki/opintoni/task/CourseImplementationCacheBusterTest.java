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
import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;

import java.time.Instant;
import java.util.Locale;

import static fi.helsinki.opintoni.web.TestConstants.TEACHER_COURSE_REALISATION_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class CourseImplementationCacheBusterTest extends SpringTest {
    @Autowired
    private CoursePageClient coursePageRestClient;

    @Autowired
    private CourseImplementationCacheBuster courseImplementationCacheBuster;

    @Autowired
    private CacheManager persistentCacheManager;

    private ValueWrapper getCourseImplementationFromCache(String implementationId, Locale locale) {
        return persistentCacheManager.getCache(CacheConstants.COURSE_PAGE).get(String.format("%s_%s", implementationId, locale.toString()));
    }

    public void assertCourseImplementationCacheBustForLocale(Locale locale) {
        defaultTeacherRequestChain().courseImplementationWithLocale(locale);
        expectCourseImplementationChangesRequest();

        CoursePageCourseImplementation implementations = coursePageRestClient.getCoursePage(TEACHER_COURSE_REALISATION_ID, locale);

        ValueWrapper implementationsFromCache = getCourseImplementationFromCache(TEACHER_COURSE_REALISATION_ID, locale);

        assertThat(implementations).isEqualToComparingFieldByFieldRecursively(implementationsFromCache.get());

        courseImplementationCacheBuster.checkForUpdatedCourseImplementations(Instant.now().getEpochSecond());

        implementationsFromCache = getCourseImplementationFromCache(TEACHER_COURSE_REALISATION_ID, locale);

        assertThat(implementationsFromCache).isNull();
    }

    @Test
    public void thatCourseImplementationCacheIsBustedForFinnishLocale() {
        assertCourseImplementationCacheBustForLocale(new Locale("fi"));
    }

    @Test
    public void thatCourseImplementationCacheIsBustedForEnglishLocale() {
        assertCourseImplementationCacheBustForLocale(Locale.ENGLISH);
    }

    @Test
    public void thatCourseImplementationCacheIsBustedForSwedishLocale() {
        assertCourseImplementationCacheBustForLocale(new Locale("sv"));
    }
}
