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

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.integration.coursepage.CoursePageCourseImplementation;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CourseImplementationCacheOperations {
    @CachePut(value = CacheConstants.COURSE_PAGE,
        key = "#implementation.courseImplementationId + '_' + #locale.getLanguage()",
        cacheManager = "persistentCacheManager")
    public CoursePageCourseImplementation insertOrUpdateCoursePageCourseImplementationInCache(
        CoursePageCourseImplementation implementation, Locale locale) {

        return implementation;
    }
}
