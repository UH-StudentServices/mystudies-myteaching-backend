/*
 * This file is part of MystudiesMyteaching application.
 *
 * MystudiesMyteaching application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MystudiesMyteaching application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MystudiesMyteaching application.  If not, see <http://www.gnu.org/licenses/>.
 */

package fi.helsinki.opintoni.integration.iam;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

public class IAMMockClient implements IAMClient {

    private static final String INACTIVE_ACCOUNT = "inactiveuser";
    private static final String NOT_FOUND_ACCOUNT = "notfound";

    @Value("${inactiveUserCleaner.testUserToInactivate:#{null}}")
    private String testUserToInactivate;

    @Override
    public Optional<AccountStatus> getAccountStatus(String username) {
        if (username.contains(INACTIVE_ACCOUNT) || username.equals(testUserToInactivate)) {
            return getInactiveAccountStatus(username);
        }

        if (username.contains(NOT_FOUND_ACCOUNT)) {
            return Optional.empty();
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
