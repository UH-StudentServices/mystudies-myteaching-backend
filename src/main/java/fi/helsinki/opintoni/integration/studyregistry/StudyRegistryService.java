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

package fi.helsinki.opintoni.integration.studyregistry;

import fi.helsinki.opintoni.cache.CacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataSet.COURSE_REALISATION_TEACHERS;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataSet.PERSON_INFO;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataSet.STUDENT_ENROLLMENTS;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataSet.STUDENT_EVENTS;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataSet.STUDY_ATTAINMENTS;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataSet.STUDY_RIGHTS;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataSet.TEACHER_COURSES;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataSet.TEACHER_EVENTS;

@Service
public class StudyRegistryService {
    @Autowired
    @Qualifier("oodiStudyRegistry")
    private StudyRegistry oodiStudyRegistry;

    @Autowired
    @Qualifier("sisuStudyRegistry")
    private StudyRegistry sisuStudyRegistry;

    @Autowired
    private StudyRegistryDataSetConfiguration studyRegistryDataSetConfiguration;

    private StudyRegistry getStudyRegistry(StudyRegistryDataSet dataSet) {
        if (studyRegistryDataSetConfiguration.getSisuDataSets().contains(dataSet.name())) {
            return sisuStudyRegistry;
        }
        return oodiStudyRegistry;
    }

    @Cacheable(value = CacheConstants.STUDENT_ENROLLMENTS, cacheManager = "transientCacheManager")
    public List<Enrollment> getEnrollments(String studentNumber) {
        return getStudyRegistry(STUDENT_ENROLLMENTS).getEnrollments(studentNumber);
    }

    @Cacheable(value = CacheConstants.STUDENT_EVENTS, cacheManager = "transientCacheManager")
    public List<Event> getStudentEvents(String studentNumber) {
        return getStudyRegistry(STUDENT_EVENTS).getStudentEvents(studentNumber);
    }

    @Cacheable(value = CacheConstants.TEACHER_EVENTS, cacheManager = "transientCacheManager")
    public List<Event> getTeacherEvents(String teacherNumber) {
        return getStudyRegistry(TEACHER_EVENTS).getTeacherEvents(teacherNumber);
    }

    @Cacheable(value = CacheConstants.STUDY_ATTAINMENTS, cacheManager = "transientCacheManager")
    public List<StudyAttainment> getStudyAttainments(String personId) {
        return getStudyRegistry(STUDY_ATTAINMENTS).getStudyAttainments(personId);
    }

    @Cacheable(value = CacheConstants.STUDY_ATTAINMENTS, cacheManager = "transientCacheManager")
    public List<StudyAttainment> getStudyAttainments(String personId, String studentNumber) {
        return getStudyRegistry(STUDY_ATTAINMENTS).getStudyAttainments(personId, studentNumber);
    }

    @Cacheable(value = CacheConstants.TEACHER_COURSES, cacheManager = "transientCacheManager")
    public List<TeacherCourse> getTeacherCourses(String teacherNumber, String sinceDateString) {
        return getStudyRegistry(TEACHER_COURSES).getTeacherCourses(teacherNumber, sinceDateString);
    }

    @Cacheable(value = CacheConstants.STUDY_RIGHTS, cacheManager = "transientCacheManager")
    public List<StudyRight> getStudentStudyRights(String studentNumber) {
        return getStudyRegistry(STUDY_RIGHTS).getStudentStudyRights(studentNumber);
    }

    @Cacheable(value = CacheConstants.COURSE_UNIT_REALISATION_TEACHERS, cacheManager = "transientCacheManager")
    public List<Teacher> getCourseRealisationTeachers(String realisationId) {
        return getStudyRegistry(COURSE_REALISATION_TEACHERS).getCourseRealisationTeachers(realisationId);
    }

    public Person getPerson(String personId) {
        return getStudyRegistry(PERSON_INFO).getPerson(personId);
    }
}
