package fi.helsinki.opintoni.task;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.integration.coursepage.CoursePageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseImplementationCacheBuster {
    private final CoursePageClient coursePageClient;
    private final CacheManager cacheManager;

    private static Logger log = LoggerFactory.getLogger(CourseImplementationCacheBuster.class);

    @Autowired
    public CourseImplementationCacheBuster(CoursePageClient coursePageClient, CacheManager cacheManager) {
        this.coursePageClient = coursePageClient;
        this.cacheManager = cacheManager;
    }

    public void checkForUpdatedCourseImplementations(long updatesSince) {
        List<Long> updatedCourses = coursePageClient.getUpdatedCourseImplementationIds(updatesSince);
        evictStaleCacheEntries(updatedCourses);
    }

    private void evictStaleCacheEntries(List<Long> updatedCourses) {
        Cache courseImplementationCache = cacheManager.getCache(CacheConstants.COURSE_PAGE);
        updatedCourses.stream().forEach(id -> {
            log.trace("evicting cache entry for course impl with id {}", id);
            courseImplementationCache.evict(id.toString());
        });
    }
}
