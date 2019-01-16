package fi.helsinki.opintoni.integration.iam;

import java.util.Optional;

public interface IAMClient {

    Optional<AccountStatus> getAccountStatus(String username);

}
