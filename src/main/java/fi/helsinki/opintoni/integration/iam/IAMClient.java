package fi.helsinki.opintoni.integration.iam;

public interface IAMClient {

    AccountStatus getAccountStatus(String username);

}
