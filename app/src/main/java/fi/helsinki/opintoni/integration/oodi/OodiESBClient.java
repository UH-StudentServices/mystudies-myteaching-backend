package fi.helsinki.opintoni.integration.oodi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.integration.jms.JMSClient;
import fi.helsinki.opintoni.integration.jms.JMSResponseException;
import fi.helsinki.opintoni.integration.oodi.courseunitrealisation.OodiCourseUnitRealisation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jms.support.destination.DestinationResolver;

import javax.jms.ConnectionFactory;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class OodiESBClient extends JMSClient implements OodiClient {

    private static final String OODI_DATA_PROPERTY = "data";

    public static final String STUDENT_NUMBER_PARAMETER = "student_number";
    public static final String TEACHER_ID_PARAMETER = "teacher_id";
    public static final String SINCE_DATE_PARAMETER = "since_date";
    public static final String PERSON_ID_PARAMETER = "person_id";
    public static final String COURSE_ID_PARAMETER = "course_id";

    public static final String STUDENTS_ENROLLMENTS_METHOD = "doo.oodi.students.enrollments";
    public static final String STUDENTS_EVENTS_METHOD = "doo.oodi.students.events";
    public static final String TEACHERS_EVENTS_METHOD = "doo.oodi.teachers.teaching.events";
    public static final String STUDENTS_STUDYATTAINMENTS_METHOD = "doo.oodi.students.studyattainments";
    public static final String TEACHERS_TEACHING_ALL_METHOD = "doo.oodi.teachers.teaching.all";
    public static final String STUDENTS_STUDY_RIGHTS_METHOD = "doo.oodi.students.studyrights";
    public static final String COURSE_UNIT_REALISATION_METHOD = "doo.oodi.courseunitrealisations";
    public static final String STUDENT_INFO_METHOD = "doo.oodi.students.info";
    public static final String ROLES_METHOD = "doo.oodi.persons.roles";

    public OodiESBClient(ConnectionFactory connectionFactory,
                         DestinationResolver destinationResolver,
                         ObjectMapper objectMapper,
                         String requestQueueName,
                         String responseQueueName) {
        super(connectionFactory, destinationResolver, objectMapper, requestQueueName, responseQueueName);
    }

    @Override
    protected String getDataFromResponse(String response) throws JMSResponseException {
        try {
            return Optional
                .ofNullable(objectMapper.readValue(response, JsonNode.class).get(OODI_DATA_PROPERTY))
                .map(JsonNode::toString)
                .orElseThrow(() -> new JMSResponseException(response));
        } catch (IOException e) {
            throw new RuntimeException("Could not parse Oodi response to Json " + response);
        }
    }

    @Override
    @Cacheable(CacheConstants.STUDENT_ENROLLMENTS)
    public List<OodiEnrollment> getEnrollments(String studentNumber) {
        return parseResponse(queryStudentData(STUDENTS_ENROLLMENTS_METHOD, studentNumber), new TypeReference<List<OodiEnrollment>>() {});
    }

    @Override
    @Cacheable(CacheConstants.STUDENT_EVENTS)
    public List<OodiEvent> getStudentEvents(String studentNumber) {
        return parseResponse(queryStudentData(STUDENTS_EVENTS_METHOD, studentNumber), new TypeReference<List<OodiEvent>>() {});
    }

    @Override
    @Cacheable(CacheConstants.TEACHER_EVENTS)
    public List<OodiEvent> getTeacherEvents(String teacherId) {
        return parseResponse(queryTeacherData(TEACHERS_EVENTS_METHOD, teacherId), new TypeReference<List<OodiEvent>>() {});
    }

    @Override
    public List<OodiStudyAttainment> getStudyAttainments(String studentNumber) {
        return parseResponse(queryStudentData(STUDENTS_STUDYATTAINMENTS_METHOD, studentNumber), new TypeReference<List<OodiStudyAttainment>>() {});
    }

    @Override
    @Cacheable(CacheConstants.TEACHER_COURSES)
    public List<OodiTeacherCourse> getTeacherCourses(String teacherId, String sinceDateString) {
        return parseResponse(queryTeacherData(TEACHERS_TEACHING_ALL_METHOD, teacherId, sinceDateString), new TypeReference<List<OodiTeacherCourse>>() {});
    }

    @Override
    public List<OodiStudyRight> getStudentStudyRights(String studentNumber) {
        return parseResponse(queryStudentData(STUDENTS_STUDY_RIGHTS_METHOD, studentNumber), new TypeReference<List<OodiStudyRight>>() {});
    }

    @Override
    @Cacheable(CacheConstants.COURSE_UNIT_REALISATIONS)
    public OodiCourseUnitRealisation getCourseUnitRealisation(String realisationId) {
        return parseResponse(
            queryMethodWithParameters(COURSE_UNIT_REALISATION_METHOD,
                ImmutableMap.of(COURSE_ID_PARAMETER, Integer.valueOf(realisationId))),
            new TypeReference<OodiCourseUnitRealisation>() {});
    }

    @Override
    public OodiStudentInfo getStudentInfo(String studentNumber) {
        return parseResponse(
            queryStudentData(STUDENT_INFO_METHOD, studentNumber),
            new TypeReference<OodiStudentInfo>() {});
    }

    @Override
    public OodiRoles getRoles(String oodiPersonId) {
        return parseResponse(
            queryMethodWithParameters(ROLES_METHOD,
                ImmutableMap.of(PERSON_ID_PARAMETER, oodiPersonId)),
            new TypeReference<OodiRoles>() {});
    }

    private String queryStudentData(String method, String studentNumber) {
        return queryMethodWithParameters(method, ImmutableMap.of(STUDENT_NUMBER_PARAMETER, studentNumber));
    }

    private String queryTeacherData(String method, String teacherId) {
        return queryMethodWithParameters(method, ImmutableMap.of(TEACHER_ID_PARAMETER, teacherId));
    }

    private String queryTeacherData(String method, String teacherId, String sinceDateString) {
        return queryMethodWithParameters(method, ImmutableMap.of(TEACHER_ID_PARAMETER, teacherId, SINCE_DATE_PARAMETER, sinceDateString));
    }
}
