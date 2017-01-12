package fi.helsinki.opintoni.integration.esb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Optional;

public class ESBMockClient implements ESBClient {

    @Value("classpath:sampledata/esb/employeeinfo.json")
    private Resource employeeInfo;

    private final ObjectMapper objectMapper;

    public ESBMockClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<ESBEmployeeInfo> getEmployeeInfo(String employeeNumber) {
        try {
            return Optional.of(objectMapper.readValue(employeeInfo.getInputStream(), ESBEmployeeInfo.class));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
