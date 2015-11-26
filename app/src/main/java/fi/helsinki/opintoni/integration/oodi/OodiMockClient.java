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

package fi.helsinki.opintoni.integration.oodi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.helsinki.opintoni.integration.oodi.courseunitrealisation.OodiCourseUnitRealisation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class OodiMockClient implements OodiClient {

    @Value("classpath:sampledata/oodi/studentcourses.json")
    private Resource studentCourses;

    @Value("classpath:sampledata/oodi/studentevents.json")
    private Resource studentEvents;

    @Value("classpath:sampledata/oodi/studentstudyattainments.json")
    private Resource studentAttainments;

    @Value("classpath:sampledata/oodi/teacherevents.json")
    private Resource teacherEvents;

    @Value("classpath:sampledata/oodi/teachercourses.json")
    private Resource teacherCourses;

    @Value("classpath:sampledata/oodi/studentstudyrights.json")
    private Resource studentStudyRights;

    @Value("classpath:sampledata/oodi/buildings.json")
    private Resource buildings;

    @Value("classpath:sampledata/oodi/courseunitrealisation.json")
    private Resource courseUnitRealisation;

    @Value("classpath:sampledata/oodi/roles.json")
    private Resource roles;

    @Value("classpath:sampledata/oodi/studentinfo.json")
    private Resource studentInfo;

    private final ObjectMapper objectMapper;

    public OodiMockClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<OodiEnrollment> getEnrollments(String studentNumber, Locale locale) {
        return getOodiResponse(studentCourses, new TypeReference<OodiResponse<OodiEnrollment>>() {
        });
    }

    @Override
    public List<OodiEvent> getStudentEvents(String studentNumber, Locale locale) {
        return getOodiResponse(studentEvents, new TypeReference<OodiResponse<OodiEvent>>() {
        });
    }

    @Override
    public List<OodiStudyAttainment> getStudyAttainments(String studentNumber, Locale locale) {
        return getOodiResponse(studentAttainments, new TypeReference<OodiResponse<OodiStudyAttainment>>() {
        });
    }

    @Override
    public List<OodiEvent> getTeacherEvents(String teacherNumber, Locale locale) {
        return getOodiResponse(teacherEvents, new TypeReference<OodiResponse<OodiEvent>>() {
        });
    }

    @Override
    public List<OodiTeacherCourse> getTeacherCourses(String teacherNumber, Locale locale, String sinceDateString) {
        return getOodiResponse(teacherCourses, new TypeReference<OodiResponse<OodiTeacherCourse>>() {
        });
    }

    @Override
    public List<OodiStudyRight> getStudentStudyRights(String studentNumber, Locale locale) {
        return getOodiResponse(studentStudyRights, new TypeReference<OodiResponse<OodiStudyRight>>() {
        });
    }

    @Override
    public OodiCourseUnitRealisation getCourseUnitRealisation(String realisationId, Locale locale) {
        return getSingleOodiResponse(courseUnitRealisation,
            new TypeReference<OodiSingleResponse<OodiCourseUnitRealisation>>() {
            });
    }

    @Override
    public OodiStudentInfo getStudentInfo(String studentNumber) {
        return getSingleOodiResponse(studentInfo,
            new TypeReference<OodiSingleResponse<OodiStudentInfo>>() {
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
