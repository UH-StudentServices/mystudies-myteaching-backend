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

package fi.helsinki.opintoni.task;

import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.domain.UserSettings;
import fi.helsinki.opintoni.integration.iam.AccountStatus;
import fi.helsinki.opintoni.integration.iam.IAMClient;
import fi.helsinki.opintoni.repository.CalendarFeedRepository;
import fi.helsinki.opintoni.repository.FavoriteRepository;
import fi.helsinki.opintoni.repository.OfficeHoursRepository;
import fi.helsinki.opintoni.repository.TodoItemRepository;
import fi.helsinki.opintoni.repository.UsefulLinkRepository;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.repository.UserSettingsRepository;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.storage.FileStorage;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InactiveUserCleaner {

    private static final Logger log = LoggerFactory.getLogger(InactiveUserCleaner.class);

    private static final String USER_NAME_SUFFIX = "@helsinki.fi";
    private static final int MAX_BATCHES_TO_PROCESS = 1000;
    private static final int USERS_BATCH_SIZE = 100;

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final CalendarFeedRepository calendarFeedRepository;
    private final FavoriteRepository favoriteRepository;
    private final TodoItemRepository todoItemRepository;
    private final UsefulLinkRepository usefulLinkRepository;
    private final OfficeHoursRepository officeHoursRepository;
    private final ProfileRepository profileRepository;

    private final FileStorage fileStorage;
    private final IAMClient iamClient;

    @Autowired
    public InactiveUserCleaner(
        UserRepository userRepository,
        UserSettingsRepository userSettingsRepository,
        CalendarFeedRepository calendarFeedRepository,
        FavoriteRepository favoriteRepository,
        TodoItemRepository todoItemRepository,
        UsefulLinkRepository usefulLinkRepository,
        OfficeHoursRepository officeHoursRepository,
        ProfileRepository profileRepository,
        FileStorage fileStorage,
        IAMClient iamClient
    ) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.calendarFeedRepository = calendarFeedRepository;
        this.favoriteRepository = favoriteRepository;
        this.todoItemRepository = todoItemRepository;
        this.usefulLinkRepository = usefulLinkRepository;
        this.officeHoursRepository = officeHoursRepository;
        this.profileRepository = profileRepository;
        this.fileStorage = fileStorage;
        this.iamClient = iamClient;
    }

    @Async("taskExecutor")
    public void cleanInactiveUsers() {
        log.info("Start cleaning of inactive users from DB");
        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger removed = new AtomicInteger(0);
        int batches = 0;

        List<User> inactiveUsers = findInactiveUsers(USERS_BATCH_SIZE);
        do {
            inactiveUsers.forEach(user -> {
                boolean userRemoved = processUser(user);
                processed.incrementAndGet();
                if (userRemoved) {
                    removed.incrementAndGet();
                }
            });
            batches++;
        } while (!(inactiveUsers = findInactiveUsers(USERS_BATCH_SIZE)).isEmpty() && batches < MAX_BATCHES_TO_PROCESS);

        if (batches >= MAX_BATCHES_TO_PROCESS) {
            log.info(
                "Max batches to process limit ({} batches of {} users) exceeded while cleaning inactive users, processed {} users, removed {} users",
                MAX_BATCHES_TO_PROCESS, USERS_BATCH_SIZE, processed.get(), removed.get());
        } else {
            log.info("Cleaning of inactive users from DB completed, processed {} users, removed {} users", processed.get(), removed.get());
        }
    }

    /**
     * Fetches inactive users.
     *
     * @param limit How many users at most to fetch
     * @return List of at most {@code limit} inactive users
     */
    public List<User> findInactiveUsers(long limit) {
        return userRepository.findInactiveUsers(limit, 0);
    }

    /**
     * Checks user account status from IAM and removes the account if it is inactive.
     *
     * @param user user to process
     * @return {@code true} if user was removed
     */
    @Transactional
    public boolean processUser(User user) {
        AtomicBoolean userRemoved = new AtomicBoolean(false);
        fetchAccountStatus(user).ifPresentOrElse(
            status -> {
                try {
                    if (hasInactiveIamAccount(status)) {
                        deleteUserData(user);
                        log.info("User {} had inactive IAM account, deleted", user.id);
                        userRemoved.set(true);
                    } else {
                        updateAccountActiveUntilDate(user, status);
                        log.info("User {} has active IAM account, keep active", user.id);
                    }
                } catch (Exception e) {
                    log.error("Processing user {} failed, {}", user.id, e.getCause(), e);
                }
            },
            () -> {
                // Set active until date to future to avoid extra looping in batch processing
                updateAccountActiveUntilDate(user, DateTime.now().plusMonths(1));
                log.warn("User {} not found in IAM, skipping", user.id);
            }
        );
        return userRemoved.get();
    }

    public Optional<AccountStatus> fetchAccountStatus(User user) {
        String username = user.eduPersonPrincipalName.replace(USER_NAME_SUFFIX, "");
        return iamClient.getAccountStatus(username);
    }

    private boolean hasInactiveIamAccount(AccountStatus status) {
        return new DateTime(status.endDate).isBefore(DateTime.now());
    }

    private void updateAccountActiveUntilDate(User user, AccountStatus status) {
        updateAccountActiveUntilDate(user, new DateTime(status.endDate));
    }

    private void updateAccountActiveUntilDate(User user, DateTime activeUntil) {
        user.accountActiveUntilDate = activeUntil;
        userRepository.save(user);
    }

    private void deleteUserData(User user) {
        calendarFeedRepository.deleteByUserId(user.id);
        todoItemRepository.deleteByUserId(user.id);
        usefulLinkRepository.deleteByUserId(user.id);
        favoriteRepository.deleteByUserId(user.id);
        officeHoursRepository.deleteByUserId(user.id);
        deleteUserSettings(user.id);

        if (hasProfile(user)) {
            user.accountStatus = User.AccountStatus.INACTIVE;
            userRepository.save(user);
        } else {
            userRepository.delete(user);
        }
    }

    private boolean hasProfile(User user) {
        return profileRepository.findByUserId(user.id).iterator().hasNext();
    }

    private void deleteUserSettings(Long userId) {
        userSettingsRepository.findFirstByUserId(userId).ifPresent(userSettings -> {
            deleteCustomBackgroundImage(userSettings);
            userSettingsRepository.deleteByUserId(userId);
        });
    }

    private void deleteCustomBackgroundImage(UserSettings userSettings) {
        if (userSettings.uploadedBackgroundFilename != null) {
            fileStorage.remove(userSettings.uploadedBackgroundFilename);
        }
    }
}
