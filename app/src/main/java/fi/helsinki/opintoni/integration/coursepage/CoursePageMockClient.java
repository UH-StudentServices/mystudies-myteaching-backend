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

    public CoursePageMockClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public CoursePageCourseImplementation getCoursePage(String courseImplementationId, Locale locale) {
        Resource courses = (courseImplementationId != null) ? course1 : course2;
        return getResponse(courses, new TypeReference<List<CoursePageCourseImplementation>>() {
        }).get(0);
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
