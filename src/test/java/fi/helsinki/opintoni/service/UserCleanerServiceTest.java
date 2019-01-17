package fi.helsinki.opintoni.service;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.integration.iam.IAMClient;
import fi.helsinki.opintoni.repository.*;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.storage.FileStorage;
import fi.helsinki.opintoni.service.storage.MemoryFileStorage;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class UserCleanerServiceTest extends SpringTest {

    private static final String USER_TO_DELETE = "inactiveuser@helsinki.fi";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCleanerService userCleanerService;

    @Autowired
    private CalendarFeedRepository calendarFeedRepository;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private UsefulLinkRepository usefulLinkRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private OfficeHoursRepository officeHoursRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private IAMClient iamClient;

    private User getUserToBeDeleted() {
        return userRepository.findByEduPersonPrincipalName(USER_TO_DELETE).orElse(null);
    }

    @Test
    public void thatCalendarFeedIsDeleted() {
        User user = getUserToBeDeleted();

        assertThat(calendarFeedRepository.findByUserId(user.id).isPresent()).isTrue();
        userCleanerService.cleanInactiveUsers();
        assertThat(calendarFeedRepository.findByUserId(user.id).isPresent()).isFalse();
    }

    @Test
    public void thatTodoItemsAreDeleted() {
        User user = getUserToBeDeleted();

        assertThat(todoItemRepository.findByUserId(user.id).isEmpty()).isFalse();
        userCleanerService.cleanInactiveUsers();
        assertThat(todoItemRepository.findByUserId(user.id).isEmpty()).isTrue();
    }

    @Test
    public void thatUsefulLinksAreDeleted() {
        User user = getUserToBeDeleted();

        assertThat(usefulLinkRepository.findByUserIdOrderByOrderIndexAsc(user.id).isEmpty()).isFalse();
        userCleanerService.cleanInactiveUsers();
        assertThat(usefulLinkRepository.findByUserIdOrderByOrderIndexAsc(user.id).isEmpty()).isTrue();
    }

    @Test
    public void thatFavoritesAreDeleted() {
        User user = getUserToBeDeleted();

        assertThat(favoriteRepository.findByUserIdOrderByOrderIndexAsc(user.id).isEmpty()).isFalse();
        userCleanerService.cleanInactiveUsers();
        assertThat(favoriteRepository.findByUserIdOrderByOrderIndexAsc(user.id).isEmpty()).isTrue();
    }

    @Test
    public void thatOfficeHoursAreDeleted() {
        User user = getUserToBeDeleted();

        assertThat(officeHoursRepository.findByUserId(user.id).isEmpty()).isFalse();
        userCleanerService.cleanInactiveUsers();
        assertThat(officeHoursRepository.findByUserId(user.id).isEmpty()).isTrue();
    }

    @Test
    public void thatOnlyInactiveUsersWithoutPortfolioAreDeletedFromUserAccountTable() {
        int sizeBefore = userRepository.findAll().size();

        userCleanerService.cleanInactiveUsers();
        assertThat(userRepository.findAll().size()).isEqualTo(sizeBefore - 1);
    }

    @Test
    public void thatUploadedImageIsDeletedFromFileService() {
        FileStorage fileStorage = mock(MemoryFileStorage.class);

        new UserCleanerService(
            userRepository,
            userSettingsRepository,
            calendarFeedRepository,
            favoriteRepository,
            todoItemRepository,
            usefulLinkRepository,
            officeHoursRepository,
            profileRepository,
            fileStorage,
            iamClient
        ).cleanInactiveUsers();

        Mockito.verify(fileStorage, times(1)).remove("uploaded_background.jpg");
    }
}
