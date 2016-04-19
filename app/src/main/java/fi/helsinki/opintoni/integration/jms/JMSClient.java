package fi.helsinki.opintoni.integration.jms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.JmsUtils;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.util.StopWatch;

import javax.jms.*;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public abstract class JMSClient {

    private final String MESSAGE_SELECTOR_TEMPLATE = "JMSCorrelationID = '%s'";
    private final String METHOD_PROPERTY = "method";
    private final String ESB_REPLY_TO_PROPERTY = "esb_reply_to";

    private final static Logger LOGGER = LoggerFactory.getLogger(JMSClient.class);

    private static final int SYNC_TIMEOUT = 10000;

    private final String requestQueueName;
    private final String responseQueueName;

    protected final ObjectMapper objectMapper;

    private final ConnectionFactory connectionFactory;
    private final DestinationResolver destinationResolver;

    public JMSClient(ConnectionFactory connectionFactory,
                     DestinationResolver destinationResolver,
                     ObjectMapper objectMapper, String requestQueueName, String responseQueueName) {
        this.connectionFactory = connectionFactory;
        this.destinationResolver = destinationResolver;
        this.objectMapper = objectMapper;
        this.requestQueueName = requestQueueName;
        this.responseQueueName = responseQueueName;
    }

    private TextMessage sendMessage(String method, Map<String, Object> parameters) {
        Session session = null;
        Connection connection = null;
        StopWatch stopWatch = new StopWatch();

        try {
            stopWatch.start();
            //When using PoolingConnectionFactory, this will get the connection from pool.
            connection = connectionFactory.createConnection();
            connection.start();
            //When using PoolingConnectionFactory, this will get the session from pool.
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            return sendSynchronousMessage(session, method, parameters);
        } catch (JMSException e) {
            throw new RuntimeException("Failed to create jMS session");
        } finally {
            //Close session and connection and return them back to pool when using PoolingConnectionFactory
            JmsUtils.closeSession(session);
            JmsUtils.closeConnection(connection);

            stopWatch.stop();
            LOGGER.info(String.format("Response for %s took %s seconds", method, stopWatch.getTotalTimeSeconds()));
        }
    }

    protected String queryMethodWithParameters(String method, Map<String, Object> parameters) {
        String response = "";
        try {
            response = sendMessage(method, parameters).getText();
            return getDataFromResponse(response);
        } catch (JMSResponseException e) {
            LOGGER.error(
                String.format(
                    "JMS method call failed. Queue: %s, method: %s, parameters: %s, response: %s",
                    requestQueueName,
                    method,
                    Arrays.toString(parameters.entrySet().toArray()),
                    response));
            throw new RuntimeException("JMS method call failed for " + method);
        } catch (JMSException e) {
            throw new RuntimeException("Failed to extract TextMessage text " + method);
        }
    }

    protected <T> T parseResponse(String response, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(response, typeReference);
        } catch (Exception e) {
            LOGGER.error("Error parsing JMS response: " + response);
            throw new RuntimeException("JMS response parsing failed");
        }
    }

    protected abstract String getDataFromResponse(String response) throws JMSResponseException;

    private TextMessage sendSynchronousMessage(Session session, String method, Map<String, Object> parameters) {
        try {
            String correlationId = createCorrelationId();

            Destination requestQueue = destinationResolver.resolveDestinationName(session, requestQueueName, false);
            Destination responseQueue = destinationResolver.resolveDestinationName(session, responseQueueName, false);

            MessageProducer producer = session.createProducer(requestQueue);
            MessageConsumer consumer = session.createConsumer(responseQueue, createMessageSelector(correlationId));

            TextMessage textMessage = createTextMessage(session, method, parameters, correlationId);

            producer.send(requestQueue, textMessage);

            return (TextMessage)consumer.receive(SYNC_TIMEOUT);

        } catch(JMSException e) {
            throw new RuntimeException("Error when receiving response from " + method);
        }
    }

    private TextMessage createTextMessage(Session session,
                                          String method,
                                          Map<String, Object> parameters,
                                          String correlationId) throws JMSException{
        TextMessage textMessage = session.createTextMessage();
        textMessage.setStringProperty(METHOD_PROPERTY, method);
        textMessage.setJMSCorrelationID(correlationId);
        textMessage.setJMSReplyTo(destinationResolver.resolveDestinationName(session, responseQueueName, false));
        textMessage.setStringProperty(ESB_REPLY_TO_PROPERTY, responseQueueName);

        for(String key : parameters.keySet()) {
            textMessage.setObjectProperty(key, parameters.get(key));
        }
        //Set text to empty string so that @JmsListeners don't throw exception.
        textMessage.setText("");

        return textMessage;
    }

    private String createCorrelationId() {
        return UUID.randomUUID().toString();
    }

    private String createMessageSelector(String correlationId) {
        return String.format(MESSAGE_SELECTOR_TEMPLATE, correlationId);
    }
}
