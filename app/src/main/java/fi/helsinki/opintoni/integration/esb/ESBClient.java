package fi.helsinki.opintoni.integration.esb;

import java.util.Optional;

public interface ESBClient {
    Optional<ESBEmployeeInfo> getEmployeeInfo(String employeeNumber);
}
