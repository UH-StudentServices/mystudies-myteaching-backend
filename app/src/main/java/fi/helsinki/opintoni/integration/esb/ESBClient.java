package fi.helsinki.opintoni.integration.esb;

import java.util.List;

public interface ESBClient {
    List<ESBEmployeeInfo> getEmployeeInfo(String employeeNumber);
}
