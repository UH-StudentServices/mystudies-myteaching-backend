package fi.helsinki.opintoni.integration.iam;

import org.joda.time.DateTime;

import java.util.Optional;

public class IAMMockClient implements IAMClient {

    private static final String INACTIVE_ACCOUNT = "inactiveuser";

    @Override
    public Optional<AccountStatus> getAccountStatus(String username) {
        if (username.contains(INACTIVE_ACCOUNT)) {
            return getInactiveAccountStatus(username);
        }

        return getActiveAccountStatus(username);
    }

    private Optional<AccountStatus> getActiveAccountStatus(String username) {
        AccountStatus status = new AccountStatus();

        status.endDate = DateTime.now().plusYears(1).getMillis();
        status.username = username;

        return Optional.of(status);
    }

    private Optional<AccountStatus> getInactiveAccountStatus(String username) {
        AccountStatus status = new AccountStatus();

        status.endDate = DateTime.now().minusMonths(6).getMillis();
        status.username = username;

        return Optional.of(status);
    }

}
