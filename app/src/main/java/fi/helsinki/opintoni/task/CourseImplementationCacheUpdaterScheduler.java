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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("coursePage.checkUpdates.interval.seconds")
public class CourseImplementationCacheUpdaterScheduler {
    private final CourseImplementationCacheUpdater courseImplementationCacheUpdater;

    @Autowired
    public CourseImplementationCacheUpdaterScheduler(CourseImplementationCacheUpdater courseImplementationCacheUpdater) {
        this.courseImplementationCacheUpdater = courseImplementationCacheUpdater;
    }

    @Scheduled(fixedDelayString = "${coursePage.checkUpdates.interval.seconds}000")
    public void getChancedCourseImplementationsAndUpdateCached() {
        courseImplementationCacheUpdater.getChancedCourseImplementationsAndUpdateCached();
    }
}
