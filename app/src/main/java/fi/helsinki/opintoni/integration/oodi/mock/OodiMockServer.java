package fi.helsinki.opintoni.integration.oodi.mock;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Conditional(EnableMockActiveMQ.class)
public class OodiMockServer {
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

    @Value("classpath:sampledata/oodi/courseunitrealisation.json")
    private Resource courseUnitRealisation;

    @Value("classpath:sampledata/oodi/courseunitrealisationcancelled.json")
    private Resource courseUnitRealisationCancelled;

    @Value("classpath:sampledata/oodi/roles.json")
    private Resource roles;

    @Value("classpath:sampledata/oodi/studentinfo.json")
    private Resource studentInfo;

    private String parseResource(Resource resource) {
        try {
            return IOUtils.toString(resource.getInputStream());
        } catch (Exception e) {
            return "";
        }
    }

    public String getStudentCourses(String studentNumber) {
        return parseResource(studentCourses);
    }

    public String getStudentEvents(String studentNumber) {
        return parseResource(studentEvents);
    }

    public String getStudentStudyAttainments(String studentNumber) {
        return parseResource(studentAttainments);
    }

    public String getTeacherEvents(String teacherId) {
        return parseResource(teacherEvents);
    }

    public String getTeacherTeaching(String teacherId, String sinceDate) {
        return parseResource(teacherCourses);
    }

    public String getStudentStudyRights(String studentNumber) {
        return parseResource(studentStudyRights);
    }

    public String getCourseUnitRealisation(String realisationId) {
        return parseResource(courseUnitRealisation);
    }

    public String getRoles(String personId) {
        return parseResource(roles);
    }

    public String getStudentInfo(String studentNumber) {
        return parseResource(studentInfo);
    }
}
