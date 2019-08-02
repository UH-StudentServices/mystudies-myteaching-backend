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
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class InactiveUserCleaner {

    private static final Logger log = LoggerFactory.getLogger(InactiveUserCleaner.class);

    private static final String USER_NAME_SUFFIX = "@helsinki.fi";

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

    public void cleanInactiveUsers() {
        log.info("Start cleaning of inactive users from DB");
        findInactiveUsers().forEach(this::processUser);
    }

    public List<User> findInactiveUsers() {
        return userRepository.findInactiveUsers();
    }

    private void processUser(User user) {
        try {
            AccountStatus status = fetchAccountStatus(user);

            if (hasInactiveIamAccount(status)) {
                deleteUserData(user);
            } else {
                updateAccountActiveUntilDate(user, status);
            }
        } catch (Exception e) {
            log.error("Processing user {} failed", user.id, e);
        }
    }

    public AccountStatus fetchAccountStatus(User user) {
        String username = user.eduPersonPrincipalName.replace(USER_NAME_SUFFIX, "");
        return iamClient.getAccountStatus(username).orElseThrow(IllegalStateException::new);
    }

    private boolean hasInactiveIamAccount(AccountStatus status) {
        return new DateTime(status.endDate).isBefore(DateTime.now());
    }

    private void updateAccountActiveUntilDate(User user, AccountStatus status) {
        user.accountActiveUntilDate = new DateTime(status.endDate);
        userRepository.save(user);
    }

    private void deleteUserData(User user) {
        calendarFeedRepository.deleteByUserId(user.id);
        todoItemRepository.deleteByUserId(user.id);
        usefulLinkRepository.deleteByUserId(user.id);
        favoriteRepository.deleteByUserId(user.id);
        officeHoursRepository.deleteByUserId(user.id);
        deleteUserSettings(user.id);

        if (!hasProfile(user)) {
            userRepository.delete(user);
        } else {
            user.accountStatus = User.AccountStatus.INACTIVE;
            userRepository.save(user);
        }
    }

    private boolean hasProfile(User user) {
        return profileRepository.findByUserId(user.id).iterator().hasNext();
    }

    private void deleteUserSettings(Long userId) {
        Optional<UserSettings> userSettings = userSettingsRepository.findByUserId(userId);

        if (!userSettings.isEmpty()) {
            deleteCustomBackgroundImage(userSettings.get());
            userSettingsRepository.deleteByUserId(userId);
        }
    }

    private void deleteCustomBackgroundImage(UserSettings userSettings) {
        if (userSettings.uploadedBackgroundFilename != null) {
            fileStorage.remove(userSettings.uploadedBackgroundFilename);
        }
    }
}
