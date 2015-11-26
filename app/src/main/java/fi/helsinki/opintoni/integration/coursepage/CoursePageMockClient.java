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

package fi.helsinki.opintoni.integration.coursepage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CoursePageMockClient implements CoursePageClient {

    @Value("classpath:sampledata/coursepage/events.json")
    private Resource events;

    @Value("classpath:sampledata/coursepage/courses-1.json")
    private Resource courses1;

    @Value("classpath:sampledata/coursepage/courses-2.json")
    private Resource courses2;

    @Value("classpath:sampledata/coursepage/notifications.json")
    private Resource notifications;

    private final ObjectMapper objectMapper;

    public CoursePageMockClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public List<CoursePageEvent> getEvents(String courseImplementationId) {
        return getResponse(events, new TypeReference<List<CoursePageEvent>>() {
        });
    }

    @Override
    public CoursePageCourseImplementation getCoursePage(String courseImplementationId) {
        Resource courses = (courseImplementationId != null) ? courses1 : courses2;
        return getResponse(courses, new TypeReference<CoursePageCourseImplementation>() {
        });
    }

    @Override
    public List<CoursePageNotification> getCoursePageNotifications(Set<String> courseImplementationIds,
                                                                   LocalDateTime from,
                                                                   Locale locale) {
        return getResponse(notifications, new TypeReference<List<CoursePageNotification>>() {
        });
    }

    public <T> T getResponse(Resource resource, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(resource.getInputStream(), typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
