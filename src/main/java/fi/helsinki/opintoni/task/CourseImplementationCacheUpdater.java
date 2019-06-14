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

import fi.helsinki.opintoni.domain.CachedItemUpdatesCheck;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import fi.helsinki.opintoni.integration.coursepage.CoursePageIntegrationException;
import fi.helsinki.opintoni.repository.CachedItemUpdatesCheckRepository;
import fi.helsinki.opintoni.service.TimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static fi.helsinki.opintoni.cache.CacheConstants.COURSE_PAGE;
import static java.util.Arrays.asList;

@Component
public class CourseImplementationCacheUpdater {
    private static final int FIRST_UPDATE_CHECK_RADIUS = 1;

    private String[] availableLanguages;
    private final CoursePageClient coursePageClient;
    private final CachedItemUpdatesCheckRepository cachedItemUpdatesCheckRepository;
    private final CacheManager persistentCacheManager;
    private final CourseImplementationCacheOperations courseImplementationCache;

    private static final Logger log = LoggerFactory.getLogger(CourseImplementationCacheUpdater.class);

    @Autowired
    public CourseImplementationCacheUpdater(CoursePageClient coursePageClient,
                                            CachedItemUpdatesCheckRepository cachedItemUpdatesCheckRepository,
                                            CacheManager persistentCacheManager,
                                            CourseImplementationCacheOperations courseImplementationCache,
                                            @Value("${language.available}") String[] availableLanguages) {
        this.coursePageClient = coursePageClient;
        this.cachedItemUpdatesCheckRepository = cachedItemUpdatesCheckRepository;
        this.persistentCacheManager = persistentCacheManager;
        this.courseImplementationCache = courseImplementationCache;
        this.availableLanguages = availableLanguages;
    }

    public void getCourseImplementationChangesAndUpdateCache() {
        CachedItemUpdatesCheck cachedItemUpdatesCheck = cachedItemUpdatesCheckRepository.findByCacheName(COURSE_PAGE)
            .orElseGet(this::initialCourseImplementationUpdatesCheck);

        log.info("checking for course implementation updates since {}", cachedItemUpdatesCheck.lastChecked);

        LocalDateTime updateCheckDateTime = LocalDateTime.now();

        List<Long> updatedCourses =
            coursePageClient.getUpdatedCourseImplementationIds(
                cachedItemUpdatesCheck.lastChecked.atZone(TimeService.HELSINKI_ZONE_ID).toEpochSecond());

        log.info(" got course ids: {}", updatedCourses);

        if (!updatedCourses.isEmpty()) {
            updateCourseCache(updatedCourses);
        }

        cachedItemUpdatesCheck.lastChecked = updateCheckDateTime;

        cachedItemUpdatesCheckRepository.save(cachedItemUpdatesCheck);
    }

    private CachedItemUpdatesCheck initialCourseImplementationUpdatesCheck() {
        CachedItemUpdatesCheck cachedItemUpdatesCheck = new CachedItemUpdatesCheck();
        cachedItemUpdatesCheck.cacheName = COURSE_PAGE;
        cachedItemUpdatesCheck.lastChecked = initialCourseImplementationUpdateCheckDateTime();
        return cachedItemUpdatesCheck;
    }

    private LocalDateTime initialCourseImplementationUpdateCheckDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(jvmStartTime()), TimeService.HELSINKI_ZONE_ID)
            .minusHours(FIRST_UPDATE_CHECK_RADIUS);
    }

    private long jvmStartTime() {
        return ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    private void updateCourseCache(List<Long> updatedCourseIds) {
        List<String> courseIdsAsString = updatedCourseIds.stream()
            .map(Object::toString)
            .collect(Collectors.toList());

        asList(availableLanguages).stream()
            .map(Locale::new)
            .forEach(locale -> updateCourseCacheForLocale(courseIdsAsString, locale));
    }

    private void updateCourseCacheForLocale(List<String> courseIds, Locale locale) {
        List<CoursePageCourseImplementation> courses = coursePageClient.getCoursePages(courseIds, locale);
        List<String> receivedCourseIds = courses.stream().map(c -> c.courseImplementationId.toString()).collect(Collectors.toList());
        if (!receivedCourseIds.containsAll(courseIds)) {
            throw new CoursePageIntegrationException(String.format("Asked for course ids %s but got %s",
                    String.join(",", courseIds), String.join(",", receivedCourseIds)));
        }
        courses.stream()
            .forEach(courseImplementation
                -> courseImplementationCache.insertOrUpdateCoursePageCourseImplementationInCache(courseImplementation, locale));
    }
}
