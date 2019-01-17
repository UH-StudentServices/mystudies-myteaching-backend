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

package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.domain.UserSettings;
import fi.helsinki.opintoni.integration.iam.AccountStatus;
import fi.helsinki.opintoni.integration.iam.IAMClient;
import fi.helsinki.opintoni.repository.*;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.storage.FileStorage;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserCleanerService {

    private static final Logger log = LoggerFactory.getLogger(UserCleanerService.class);

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
    public UserCleanerService(
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

    @Scheduled(cron = "0 0 5 * * *")
    public void cleanInactiveUsers() {
        log.info("Start cleaning of inactive users from DB");
        findInactiveUsers().forEach(this::processUser);
    }

    public List<User> findInactiveUsers() {
        return userRepository.findInactiveUsers(DateTime.now().minusYears(1), User.AccountStatus.ACTIVE, DateTime.now());
    }

    private void processUser(User user) {
        Optional<AccountStatus> status = fetchAccountStatus(user);

        if (!status.isPresent()) {
            return;
        }

        if (hasActiveIamAccount(status.get())) {
            deleteUserData(user);
        } else {
            updateAccountActiveUntilDate(user, status.get());
        }
    }

    private Optional<AccountStatus> fetchAccountStatus(User user) {
        String username = user.eduPersonPrincipalName.replace("@helsinki.fi", "");
        return iamClient.getAccountStatus(username);
    }

    private boolean hasActiveIamAccount(AccountStatus status) {
        return new DateTime(status.endDate).isBefore(DateTime.now());
    }

    private void updateAccountActiveUntilDate(User user, AccountStatus status) {
        user.accountActiveUntilDate = new DateTime(status.endDate);
        userRepository.save(user);
    }

    private void deleteUserData(User user) {
        log.info("Deleting user data for user {}", user.id);

        try {
            calendarFeedRepository.deleteByUserId(user.id);
            todoItemRepository.deleteByUserId(user.id);
            usefulLinkRepository.deleteByUserId(user.id);
            favoriteRepository.deleteByUserId(user.id);
            officeHoursRepository.deleteByUserId(user.id);
            deleteUserSettings(user.id);

            if (!hasProfile(user)) {
                userRepository.delete(user);
            }
        } catch (Exception e) {
            log.error("Deleting user data failed for user {}", user.id, e);
        }
    }

    private boolean hasProfile(User user) {
        return profileRepository.findByUserId(user.id).iterator().hasNext();
    }

    private void deleteUserSettings(Long userId) {
        UserSettings userSettings = userSettingsRepository.findByUserId(userId);

        if (userSettings != null) {
            deleteCustomBackgroundImage(userSettings);
            userSettingsRepository.deleteByUserId(userId);
        }
    }

    private void deleteCustomBackgroundImage(UserSettings userSettings) {
        if (userSettings.uploadedBackgroundFilename != null) {
            fileStorage.remove(userSettings.uploadedBackgroundFilename);
        }
    }
}
