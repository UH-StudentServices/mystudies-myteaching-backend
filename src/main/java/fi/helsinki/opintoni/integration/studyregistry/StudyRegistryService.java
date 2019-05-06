package fi.helsinki.opintoni.integration.studyregistry;

import fi.helsinki.opintoni.cache.CacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataset.COURSE_REALISATION_TEACHERS;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataset.PERSON_INFO;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataset.STUDENT_ENROLLMENTS;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataset.STUDENT_EVENTS;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataset.STUDY_ATTAINMENTS;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataset.STUDY_RIGHTS;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataset.TEACHER_COURSES;
import static fi.helsinki.opintoni.integration.studyregistry.StudyRegistryDataset.TEACHER_EVENTS;

@Service
public class StudyRegistryService {
    @Autowired
    @Qualifier("oodiStudyRegistry")
    private StudyRegistry oodiStudyRegistry;

    @Autowired
    @Qualifier("sisuStudyRegistry")
    private StudyRegistry sisuStudyRegistry;

    @Autowired
    private StudyRegistryConfiguration studyRegistryConfiguration;

    private StudyRegistry getStudyRegistry(StudyRegistryDataset dataset) {
        if (studyRegistryConfiguration.getSisuDataSets().contains(dataset.name())) {
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
        return getStudyRegistry(TEACHER_EVENTS).getStudentEvents(teacherNumber);
    }

    public List<StudyAttainment> getStudyAttainments(String studentNumber) {
        return getStudyRegistry(STUDY_ATTAINMENTS).getStudyAttainments(studentNumber);
    }

    @Cacheable(value = CacheConstants.TEACHER_COURSES, cacheManager = "transientCacheManager")
    public List<TeacherCourse> getTeacherCourses(String teacherNumber, String sinceDateString) {
        return getStudyRegistry(TEACHER_COURSES).getTeacherCourses(teacherNumber, sinceDateString);
    }

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
