package fi.helsinki.opintoni.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("coursePage.checkUpdates.interval.seconds")
public class CourseImplementationCacheUpdaterScheduler {
    private final CourseImplementationCacheUpdater courseImplementationCacheBuster;

    @Autowired
    public CourseImplementationCacheUpdaterScheduler(CourseImplementationCacheUpdater courseImplementationCacheBuster) {
        this.courseImplementationCacheBuster = courseImplementationCacheBuster;
    }

    @Scheduled(fixedDelayString = "${coursePage.checkUpdates.interval.seconds}000")
    public void getUpdatedCourseImplementationsAndEvictFromCache() {
        courseImplementationCacheBuster.getUpdatedCourseImplementationsAndEvictFromCache();
    }
}
