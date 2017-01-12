package fi.helsinki.opintoni.integration.esb;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ESBEmployeeInfoRecord {
    public String recordType;
    public String workNumber;
    public String workMobile;
    public String workAddress;
    public String workPostcode;
    public String title;
    public String email;
    public List<ESBEmployeeInfoOrganization> hrOrganisations = newArrayList();
    public List<ESBEmployeeInfoOrganization> ocOrganisations = newArrayList();
}
