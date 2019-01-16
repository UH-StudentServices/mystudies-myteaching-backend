package fi.helsinki.opintoni.integration.iam;

import org.joda.time.DateTime;

public class IamMockClient implements IAMClient {

    private static final String INACTIVE_ACCOUNT = "inactiveuser";

    @Override
    public AccountStatus getAccountStatus(String username) {
        if (INACTIVE_ACCOUNT.equals(username)) {
            return getInactiveAccountStatus(username);
        }

        return getActiveAccountStatus(username);
    }

    private AccountStatus getActiveAccountStatus(String username) {
        AccountStatus status = new AccountStatus();

        status.endDate = DateTime.now().plusYears(1).getMillis();
        status.username = username;

        return status;
    }

    private AccountStatus getInactiveAccountStatus(String username) {
        AccountStatus status = new AccountStatus();

        status.endDate = DateTime.now().minusMonths(6).getMillis();
        status.username = username;

        return status;
    }

}
