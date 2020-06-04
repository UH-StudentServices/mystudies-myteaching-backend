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

package fi.helsinki.opintoni.integration.coursecms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static fi.helsinki.opintoni.integration.IntegrationUtil.getSisuCourseUnitRealisationId;

public class CourseCmsMockClient implements CourseCmsClient {

    private static final String TEST_COURSE_IMPLEMENTATION_ID = "234567891";
    private static final String TEST_COURSE_IMPLEMENTATION_NOT_FOUND_ID = "109155865";

    private final ObjectMapper objectMapper;

    @Value("classpath:sampledata/coursecms/course-1.json")
    private Resource course1;

    @Value("classpath:sampledata/coursecms/course-2.json")
    private Resource course2;

    public CourseCmsMockClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public CourseCmsCourseUnitRealisation getCoursePage(String curId, Locale locale) {
        if (TEST_COURSE_IMPLEMENTATION_NOT_FOUND_ID.equals(curId)) {
            CourseCmsCourseUnitRealisation emptyResult = new CourseCmsCourseUnitRealisation();
            emptyResult.courseUnitRealisationId = getSisuCourseUnitRealisationId(curId);
            return emptyResult;
        }

        Resource course = curId.equals(TEST_COURSE_IMPLEMENTATION_ID) ? course1 : course2;

        CourseCmsCourseUnitRealisation cur = getResponse(course, new TypeReference<>() {
        });
        cur.courseUnitRealisationId = getSisuCourseUnitRealisationId(curId);
        return cur;
    }

    @Override
    public List<CourseCmsCourseUnitRealisation> getCoursePages(List<String> curIds, Locale locale) {
        return Collections.emptyList();
    }

    private <T> T getResponse(Resource resource, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(resource.getInputStream(), typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
