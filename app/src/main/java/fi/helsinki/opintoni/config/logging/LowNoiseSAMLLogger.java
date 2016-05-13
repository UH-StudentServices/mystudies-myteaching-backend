package fi.helsinki.opintoni.config.logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.log.SAMLDefaultLogger;

public class LowNoiseSAMLLogger extends SAMLDefaultLogger {
    private final static Logger log = LoggerFactory.getLogger(LowNoiseSAMLLogger.class);

    public void log(String operation, String result, SAMLMessageContext context) {
        log(operation, result, context, SecurityContextHolder.getContext().getAuthentication(), null);
    }

    public void log(String operation, String result, SAMLMessageContext context, Exception e) {
        log(operation, result, context, SecurityContextHolder.getContext().getAuthentication(), e);
    }

    public void log(String operation, String result, SAMLMessageContext context, Authentication a, Exception e) {
        // log broken pipes caused by ClientAbortExceptions in terse, less noisy info messages
        if(e != null && StringUtils.containsIgnoreCase(ExceptionUtils.getRootCauseMessage(e), "Broken pipe")) {
            log.info("Broken pipe occurred");
        } else {
            super.log(operation, result, context, a, e);
        }
    }

    public void setLogMessages(boolean logMessages) {
        super.setLogMessages(logMessages);
    }

    public void setLogErrors(boolean logErrors) {
        super.setLogErrors(logErrors);
    }
}
