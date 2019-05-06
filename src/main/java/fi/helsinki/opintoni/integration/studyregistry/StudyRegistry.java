package fi.helsinki.opintoni.integration.studyregistry;

import java.util.List;

public interface StudyRegistry {

    List<Enrollment> getEnrollments(String studentNumber);

    List<Event> getStudentEvents(String studentNumber);

    List<Event> getTeacherEvents(String teacherNumber);

    List<StudyAttainment> getStudyAttainments(String studentNumber);

    List<TeacherCourse> getTeacherCourses(String teacherNumber, String sinceDateString);

    List<StudyRight> getStudentStudyRights(String studentNumber);

    List<Teacher> getCourseRealisationTeachers(String realisationId);

    Person getPerson(String personId);
}
