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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CoursePageMockClient implements CoursePageClient {

    @Value("classpath:sampledata/coursepage/course-1.json")
    private Resource course1;

    @Value("classpath:sampledata/coursepage/course-2.json")
    private Resource course2;

    private final ObjectMapper objectMapper;

    private static final long TEST_COURSE_IMPLEMENTATION_ID = 123456789L;
    private static final String TEST_COURSE_IMPLEMENTATION_NOT_FOUND_ID = "109155865";

    public CoursePageMockClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public CoursePageCourseImplementation getCoursePage(String courseImplementationId, Locale locale) {
        if (TEST_COURSE_IMPLEMENTATION_NOT_FOUND_ID.equals(courseImplementationId)) {
            return new CoursePageCourseImplementation();
        }

        Resource courses = ("123456789".equals(courseImplementationId)) ? course1 : course2;
        CoursePageCourseImplementation course = getResponse(courses, new TypeReference<List<CoursePageCourseImplementation>>() {
        }).get(0);

        course.events.forEach(this::updateEventDates);

        return course;
    }

    private void updateEventDates(CoursePageEvent coursePageEvent) {
        int currentYear = LocalDateTime.now().getYear();

        coursePageEvent.begin = coursePageEvent.begin.plusYears(currentYear - 1);
        coursePageEvent.end = coursePageEvent.end.plusYears(currentYear - 1);

        if (coursePageEvent.begin.getYear() == currentYear) {
            int month = LocalDateTime.now().getMonthValue();

            coursePageEvent.begin = coursePageEvent.begin.withMonth(month).plusDays(1);
            coursePageEvent.end = coursePageEvent.end.withMonth(month).plusDays(1);
        }
    }

    @Override
    public List<CoursePageCourseImplementation> getCoursePages(List<String> courseImplementationIds, Locale locale) {
        return new ArrayList<>();
    }

    @Override
    public List<Long> getUpdatedCourseImplementationIds(long timestamp) {
        return Collections.singletonList(TEST_COURSE_IMPLEMENTATION_ID);
    }

    public <T> T getResponse(Resource resource, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(resource.getInputStream(), typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
