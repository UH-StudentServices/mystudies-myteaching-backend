package fi.helsinki.opintoni.integration.oodi.mock;

import fi.helsinki.opintoni.config.AppConfiguration;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Conditional(EnableMockActiveMQ.class)
public class MockActiveMQ {
    @Autowired
    private AppConfiguration appConfiguration;

    private final static Logger LOGGER = LoggerFactory.getLogger(MockActiveMQ.class);

    @PostConstruct
    public void startMockBroker() {
        LOGGER.info("Starting ActiveMQ broker!");
        try {
            BrokerService broker = new BrokerService();
            broker.addConnector(appConfiguration.get("esb.brokerUrl"));
            broker.start();
        } catch (Exception e) {
            LOGGER.error("Failed to start ActiveMQ broker!");
        }
    }
}
