package fi.helsinki.opintoni.integration.jms;

import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.support.JmsUtils;
import org.springframework.jms.support.destination.DestinationResolver;

import javax.jms.*;
import java.util.Map;
import java.util.UUID;

public class SynchronousMessage implements SessionCallback<Message> {

    private final String MESSAGE_SELECTOR_TEMPLATE = "JMSCorrelationID = '%s'";
    private final String METHOD_PROPERTY = "method";
    private final String ESB_REPLY_TO_PROPERTY = "esb_reply_to";

    private final DestinationResolver destinationResolver;
    private final String method;
    private final Map<String, String> parameters;
    private final String requestQueueName;
    private final String responseQueueName;
    private final int requestTimeout;

    public SynchronousMessage(
        Map<String, String> parameters,
        String method,
        DestinationResolver destinationResolver,
        String requestQueueName,
        String responseQueueName,
        int requestTimeout) {

        this.parameters = parameters;
        this.method = method;
        this.destinationResolver = destinationResolver;
        this.requestQueueName = requestQueueName;
        this.responseQueueName = responseQueueName;
        this.requestTimeout = requestTimeout;
    }

    @Override
    public Message doInJms(final Session session) {
        MessageProducer producer = null;
        MessageConsumer consumer = null;

        try {
            String correlationId = createCorrelationId();

            Destination requestQueue = destinationResolver.resolveDestinationName(session, requestQueueName, false);
            Destination responseQueue = destinationResolver.resolveDestinationName(session, responseQueueName, false);

            producer = session.createProducer(requestQueue);
            consumer = session.createConsumer(responseQueue, createMessageSelector(correlationId));

            TextMessage textMessage = createTextMessage(session, correlationId, responseQueue, responseQueueName);

            producer.send(requestQueue, textMessage);

            return consumer.receive(requestTimeout);

        } catch(JMSException e) {
            throw new RuntimeException("Error when receiving response from " + method);
        } finally {
            JmsUtils.closeMessageConsumer(consumer);
            JmsUtils.closeMessageProducer(producer);
        }
    }

    private TextMessage createTextMessage(
        final Session session,
        final String correlationId,
        final Destination responseQueue,
        final String responseQueueName) throws JMSException{
        TextMessage textMessage = session.createTextMessage();
        textMessage.setStringProperty(METHOD_PROPERTY, method);
        textMessage.setJMSCorrelationID(correlationId);
        textMessage.setJMSReplyTo(responseQueue);
        textMessage.setStringProperty(ESB_REPLY_TO_PROPERTY, responseQueueName);

        for(String key : parameters.keySet()) {
            textMessage.setStringProperty(key, parameters.get(key));
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