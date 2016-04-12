package fi.helsinki.opintoni.integration.jms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

public abstract class JMSClient {
    private static final int SYNC_TIMEOUT = 10000;

    private final JmsTemplate jmsTemplate;
    private final String requestQueueName;
    private final String responseQueueName;

    protected final ObjectMapper objectMapper;

    public JMSClient(JmsTemplate jmsTemplate, ObjectMapper objectMapper, String requestQueueName, String responseQueueName) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
        this.requestQueueName = requestQueueName;
        this.responseQueueName = responseQueueName;
    }

    private TextMessage sendMessage(String method, Map<String, String> parameters) {
        return (TextMessage) jmsTemplate.execute(
            new SynchronousMessage(
                parameters,
                method,
                jmsTemplate.getDestinationResolver(),
                requestQueueName,
                responseQueueName,
                SYNC_TIMEOUT),
            true);
    }

    protected String queryMethodWithParameters(String method, Map<String, String> parameters) {
        try {
            return sendMessage(method, parameters).getText();
        } catch (JMSException e) {
            throw new RuntimeException("JMS query failed for method " + method);
        }
    }

    protected <T> T parseResponse(String response, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(getDataFromResponse(response), typeReference);
        } catch (Exception e) {
            throw new RuntimeException("JMS response parsing failed");
        }
    }

    protected <T> List<T> parseListResponse(String response, TypeReference<List<T>> typeReference ) {
        try {
            return objectMapper.readValue(getDataFromResponse(response), typeReference);
        } catch (Exception e) {
            throw new RuntimeException("JMS response parsing failed");
        }
    }

    protected abstract String getDataFromResponse(String response);
}
