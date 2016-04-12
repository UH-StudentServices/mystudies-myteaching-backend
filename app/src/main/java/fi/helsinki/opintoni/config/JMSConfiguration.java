package fi.helsinki.opintoni.config;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

@Configuration
public class JMSConfiguration {
    @Autowired
    private AppConfiguration appConfiguration;

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        return jmsTemplate;
    }

    @Bean
    public PooledConnectionFactory connectionFactory() {
        String brokerUrl = appConfiguration.get("esb.brokerUrl");
        PooledConnectionFactory connectionFactory = new PooledConnectionFactory(brokerUrl);
        return connectionFactory;
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setDestinationName(appConfiguration.get("esb.queueNames.in"));
        return container;
    }
}
