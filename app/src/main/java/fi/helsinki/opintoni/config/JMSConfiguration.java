package fi.helsinki.opintoni.config;

import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.ConnectionFactory;

@Configuration
public class JMSConfiguration {

    private static final String SSL_PREFIX = "ssl://";

    @Autowired
    private AppConfiguration appConfiguration;

    @Bean
    public ConnectionFactory connectionFactory() {
        String brokerUrl = appConfiguration.get("esb.brokerUrl");
        PooledConnectionFactory connectionFactory = new PooledConnectionFactory();
        if(brokerUrl.startsWith(SSL_PREFIX)) {
            connectionFactory.setConnectionFactory(AMQJMSConnectionFactory());
        }
        return connectionFactory;
    }

    @Bean
    public DestinationResolver destinationResolver() {
        return new DynamicDestinationResolver();
    }

    private ActiveMQSslConnectionFactory AMQJMSConnectionFactory() {
        ActiveMQSslConnectionFactory activeMQSslConnectionFactory = new ActiveMQSslConnectionFactory(appConfiguration.get("esb.brokerUrl"));
        try {
            activeMQSslConnectionFactory.setKeyStore(getCertFile(appConfiguration.get("esb.ssl.keyStore")));
            activeMQSslConnectionFactory.setTrustStore(getCertFile(appConfiguration.get("esb.ssl.trustStore")));
            activeMQSslConnectionFactory.setKeyStorePassword(appConfiguration.get("esb.ssl.keyStorePassword"));
            activeMQSslConnectionFactory.setTrustStorePassword(appConfiguration.get("esb.ssl.trustStorePassword"));
        } catch (Exception e) {
            throw new RuntimeException("Error configuring ActiveMQSslConnectionFactory");
        }
        return activeMQSslConnectionFactory;
    }

    private String getCertFile(String filePath) {
        return "file:" + filePath;
    }
}
