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

package fi.helsinki.opintoni.integration.oodi.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import fi.helsinki.opintoni.integration.oodi.*;
import fi.helsinki.opintoni.integration.oodi.courseunitrealisation.OodiCourseUnitRealisationTeacher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static fi.helsinki.opintoni.security.DevUserDetailsService.STUDENT_NUMBER_TEST_NEW_STUDENT;
import static fi.helsinki.opintoni.security.DevUserDetailsService.STUDENT_NUMBER_TEST_OPEN_UNI_STUDENT;

public class OodiMockClient implements OodiClient {

    private static final String LEARNING_OPPORTUNITY_A_ID = "405437";
    private static final String LEARNING_OPPORTUNITY_B_ID = "405438";

    @Value("classpath:sampledata/oodi/studentcourses.json")
    private Resource studentCourses;

    @Value("classpath:sampledata/oodi/studentcoursesopenuniversity.json")
    private Resource openUniversityStudentCourses;

    @Value("classpath:sampledata/oodi/studentevents.json")
    private Resource studentEvents;

    @Value("classpath:sampledata/oodi/studentstudyattainments.json")
    private Resource studentAttainments;

    @Value("classpath:sampledata/oodi/studentstudyattainmentsnone.json")
    private Resource newStudentAttainments;

    @Value("classpath:sampledata/oodi/teacherevents.json")
    private Resource teacherEvents;

    @Value("classpath:sampledata/oodi/teachercourses.json")
    private Resource teacherCourses;

    @Value("classpath:sampledata/oodi/studentstudyrights.json")
    private Resource studentStudyRights;

    @Value("classpath:sampledata/oodi/buildings.json")
    private Resource buildings;

    @Value("classpath:sampledata/oodi/courseunitrealisationteachers.json")
    private Resource getCourseUnitRealisationTeachers;

    @Value("classpath:sampledata/oodi/roles.json")
    private Resource roles;

    @Value("classpath:sampledata/oodi/studentinfo.json")
    private Resource studentInfo;

    @Value("classpath:sampledata/oodi/learningopportunity_a.json")
    private Resource learningOpportunityA;

    @Value("classpath:sampledata/oodi/learningopportunity_b.json")
    private Resource learningOpportunityB;

    private final ObjectMapper objectMapper;

    private Map<String, Resource> learningOpportunityById;

    @PostConstruct
    private void createLearningOpportunityMap() {
        this.learningOpportunityById = ImmutableMap.of(
            LEARNING_OPPORTUNITY_A_ID, learningOpportunityA,
            LEARNING_OPPORTUNITY_B_ID, learningOpportunityB);
    }

    public OodiMockClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<OodiEnrollment> getEnrollments(String studentNumber) {
        if (studentNumber.equals(STUDENT_NUMBER_TEST_OPEN_UNI_STUDENT)) {
            return getOodiResponse(openUniversityStudentCourses, new TypeReference<OodiResponse<OodiEnrollment>>() {
            });
        } else {
            return getOodiResponse(studentCourses, new TypeReference<OodiResponse<OodiEnrollment>>() {
            });
        }
    }

    @Override
    public List<OodiEvent> getStudentEvents(String studentNumber) {
        return getOodiResponse(studentEvents, new TypeReference<OodiResponse<OodiEvent>>() {
        });
    }

    @Override
    public List<OodiStudyAttainment> getStudyAttainments(String studentNumber) {
        if (studentNumber.equals(STUDENT_NUMBER_TEST_NEW_STUDENT)) {
            return getOodiResponse(newStudentAttainments, new TypeReference<OodiResponse<OodiStudyAttainment>>() {
            });
        } else {
            return getOodiResponse(studentAttainments, new TypeReference<OodiResponse<OodiStudyAttainment>>() {
            });
        }
    }

    @Override
    public List<OodiEvent> getTeacherEvents(String teacherNumber) {
        return getOodiResponse(teacherEvents, new TypeReference<OodiResponse<OodiEvent>>() {
        });
    }

    @Override
    public List<OodiTeacherCourse> getTeacherCourses(String teacherNumber, String sinceDateString) {
        return getOodiResponse(teacherCourses, new TypeReference<OodiResponse<OodiTeacherCourse>>() {
        });
    }

    @Override
    public List<OodiStudyRight> getStudentStudyRights(String studentNumber) {
        return getOodiResponse(studentStudyRights, new TypeReference<OodiResponse<OodiStudyRight>>() {
        });
    }

    @Override
    public List<OodiCourseUnitRealisationTeacher> getCourseUnitRealisationTeachers(String realisationId) {
        return getOodiResponse(getCourseUnitRealisationTeachers, new TypeReference<OodiResponse<OodiCourseUnitRealisationTeacher>>() {
        });
    }

    @Override
    public OodiRoles getRoles(String oodiPersonId) {
        return getSingleOodiResponse(roles,
            new TypeReference<OodiSingleResponse<OodiRoles>>() {
            });
    }

    public <T> List<T> getOodiResponse(Resource resource, TypeReference<OodiResponse<T>> typeReference) {
        try {
            OodiResponse<T> response = objectMapper.readValue(resource.getInputStream(), typeReference);
            return response.data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getSingleOodiResponse(Resource resource, TypeReference<OodiSingleResponse<T>> typeReference) {
        try {
            OodiSingleResponse<T> response = objectMapper.readValue(resource.getInputStream(), typeReference);
            return response.data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
