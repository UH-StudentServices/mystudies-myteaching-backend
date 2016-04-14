package fi.helsinki.opintoni.config;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class JMSConfiguration {
    @Autowired
    private AppConfiguration appConfiguration;

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }

    @Bean
    public PooledConnectionFactory connectionFactory() {
        String brokerUrl = appConfiguration.get("esb.brokerUrl");
        PooledConnectionFactory connectionFactory = new PooledConnectionFactory(brokerUrl);
        return connectionFactory;
    }
}
