package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.integration.iam.AccountStatus;
import fi.helsinki.opintoni.integration.iam.IAMClient;
import fi.helsinki.opintoni.repository.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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
        IAMClient iamClient) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.calendarFeedRepository = calendarFeedRepository;
        this.favoriteRepository = favoriteRepository;
        this.todoItemRepository = todoItemRepository;
        this.usefulLinkRepository = usefulLinkRepository;
        this.officeHoursRepository = officeHoursRepository;
        this.iamClient = iamClient;
    }

    public void cleanInactiveUsers() {
        findInactiveUsers().forEach(this::processUser);
    }

    public List<User> findInactiveUsers() {
        return userRepository.findInactiveUsers(DateTime.now().minusYears(1), User.AccountStatus.ACTIVE, DateTime.now());
    }

    private void processUser(User user) {
        AccountStatus status = fetchAccountStatus(user);

        if (status == null) {
            return;
        }

        DateTime accountActiveUntilDate = new DateTime(status.endDate);

        if (accountActiveUntilDate.isBefore(DateTime.now())) {
            try {
                deleteUserData(user);
            } catch (Exception e) {
                log.error("Deleting user data failed for user {}", user.id, e);
            }
        } else {
            updateAccountActiveUntilDate(user, accountActiveUntilDate);
        }
    }

    private AccountStatus fetchAccountStatus(User user) {
        try {
            String username = user.eduPersonPrincipalName.replace("@helsinki.fi", "");
            return iamClient.getAccountStatus(username);
        } catch (Exception e) {
            log.error("Fetching data from IAM failed for user {}", user.id, e);
            return null;
        }
    }

    private void updateAccountActiveUntilDate(User user, DateTime accountActiveUntilDate) {
        user.accountActiveUntilDate = accountActiveUntilDate;
        userRepository.save(user);
    }

    private void deleteUserData(User user) {
        log.info("Deleting user data for user {}", user.id);

        calendarFeedRepository.deleteByUserId(user.id);
        todoItemRepository.deleteByUserId(user.id);
        usefulLinkRepository.deleteByUserId(user.id);
        favoriteRepository.deleteByUserId(user.id);
        officeHoursRepository.deleteByUserId(user.id);
        userSettingsRepository.deleteByUserId(user.id);
    }
}
