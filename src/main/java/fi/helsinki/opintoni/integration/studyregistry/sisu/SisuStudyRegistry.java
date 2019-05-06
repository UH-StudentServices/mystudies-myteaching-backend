package fi.helsinki.opintoni.integration.studyregistry.sisu;

import fi.helsinki.opintoni.integration.studyregistry.Enrollment;
import fi.helsinki.opintoni.integration.studyregistry.Event;
import fi.helsinki.opintoni.integration.studyregistry.Person;
import fi.helsinki.opintoni.integration.studyregistry.StudyAttainment;
import fi.helsinki.opintoni.integration.studyregistry.StudyRegistry;
import fi.helsinki.opintoni.integration.studyregistry.StudyRight;
import fi.helsinki.opintoni.integration.studyregistry.Teacher;
import fi.helsinki.opintoni.integration.studyregistry.TeacherCourse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("sisuStudyRegistry")
public class SisuStudyRegistry implements StudyRegistry {

    @Override
    public List<Enrollment> getEnrollments(String studentNumber) {
        return null;
    }

    @Override
    public List<Event> getStudentEvents(String studentNumber) {
        return null;
    }

    @Override
    public List<Event> getTeacherEvents(String teacherNumber) {
        return null;
    }

    @Override
    public List<StudyAttainment> getStudyAttainments(String studentNumber) {
        return null;
    }

    @Override
    public List<TeacherCourse> getTeacherCourses(String teacherNumber, String sinceDateString) {
        return null;
    }

    @Override
    public List<StudyRight> getStudentStudyRights(String studentNumber) {
        return null;
    }

    @Override
    public List<Teacher> getCourseRealisationTeachers(String realisationId) {
        return null;
    }

    @Override
    public Person getPerson(String personId) {
        return null;
    }
}
