package fi.helsinki.opintoni.integration.oodi.mock;

import fi.helsinki.opintoni.integration.oodi.OodiESBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

@Component
@Conditional(EnableMockActiveMQ.class)
public class OodiMockESBListener {

    private static final String METHOD_PROPERTY = "method";

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private OodiMockServer oodiMockServer;

    @JmsListener(destination = "doo.opintoni.out")
    public void receiveMessage(Message message) {
        final String responseMessage = createResponseMessage(message);

        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage replyMessage = session.createTextMessage(responseMessage);
                replyMessage.setJMSCorrelationID(message.getJMSCorrelationID());
                return replyMessage;
            }
        };

        try {
            jmsTemplate.send(message.getJMSReplyTo(), messageCreator);
        } catch (Exception e) {
            throw new RuntimeException("OodiMockESBListener failed to send reply");
        }
    }

    private String createResponseMessage(Message message) {
        String responseMessage;

        switch(getProperty(message, METHOD_PROPERTY)) {

            case(OodiESBClient.STUDENTS_ENROLLMENTS_METHOD):
                responseMessage = oodiMockServer.getStudentCourses(getProperty(message, OodiESBClient.STUDENT_NUMBER_PARAMETER));
                break;
            case(OodiESBClient.STUDENTS_EVENTS_METHOD):
                responseMessage = oodiMockServer.getStudentEvents(getProperty(message, OodiESBClient.STUDENT_NUMBER_PARAMETER));
                break;
            case(OodiESBClient.STUDENTS_STUDYATTAINMENTS_METHOD):
                responseMessage = oodiMockServer.getStudentStudyAttainments(getProperty(message, OodiESBClient.STUDENT_NUMBER_PARAMETER));
                break;
            case(OodiESBClient.TEACHERS_EVENTS_METHOD):
                responseMessage = oodiMockServer.getTeacherEvents(getProperty(message, OodiESBClient.TEACHER_ID_PARAMETER));
                break;
            case(OodiESBClient.TEACHERS_TEACHING_ALL_METHOD):
                responseMessage =
                    oodiMockServer.getTeacherTeaching(
                        getProperty(message, OodiESBClient.TEACHER_ID_PARAMETER),
                        getProperty(message, OodiESBClient.SINCE_DATE_PARAMETER));
                break;
            case(OodiESBClient.STUDENTS_STUDY_RIGHTS_METHOD):
                responseMessage = oodiMockServer.getStudentStudyRights(getProperty(message, OodiESBClient.STUDENT_NUMBER_PARAMETER));
                break;
            case(OodiESBClient.COURSE_UNIT_REALISATION_METHOD):
                responseMessage = oodiMockServer.getCourseUnitRealisation(getProperty(message, OodiESBClient.COURSE_ID_PARAMETER));
                break;
            case(OodiESBClient.ROLES_METHOD):
                responseMessage = oodiMockServer.getRoles(getProperty(message, OodiESBClient.PERSON_ID_PARAMETER));
                break;
            case(OodiESBClient.STUDENT_INFO_METHOD):
                responseMessage = oodiMockServer.getStudentInfo(getProperty(message, OodiESBClient.STUDENT_NUMBER_PARAMETER));
                break;
            default:
                throw new RuntimeException("Method not found from Oodi");
        }
        return responseMessage;
    }

    private String getProperty(Message message, String property) {
        try {
            return message.getStringProperty(property);
        } catch(Exception e) {
            throw new RuntimeException("Failed to read message property " + property);
        }
    }


}
