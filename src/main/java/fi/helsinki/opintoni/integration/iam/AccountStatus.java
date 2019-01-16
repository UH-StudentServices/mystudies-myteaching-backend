package fi.helsinki.opintoni.integration.iam;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountStatus {

    @JsonProperty("username")
    public String username;

    @JsonProperty("endDate")
    public Long endDate;

}
