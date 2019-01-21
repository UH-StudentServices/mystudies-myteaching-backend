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

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.domain.profile.ProfileVisibility;
import fi.helsinki.opintoni.integration.iam.IAMClient;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.repository.*;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import fi.helsinki.opintoni.service.storage.FileStorage;
import fi.helsinki.opintoni.service.storage.MemoryFileStorage;
import fi.helsinki.opintoni.web.arguments.ProfileRole;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class InactiveUserCleanerTest extends SpringTest {

    private static final String USER_TO_DELETE = "inactiveuser@helsinki.fi";
    private static final long INACTIVE_USER_ID = 8L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InactiveUserCleaner inactiveUserCleaner;

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
        inactiveUserCleaner.cleanInactiveUsers();
        assertThat(calendarFeedRepository.findByUserId(user.id).isPresent()).isFalse();
    }

    @Test
    public void thatTodoItemsAreDeleted() {
        User user = getUserToBeDeleted();

        assertThat(todoItemRepository.findByUserId(user.id).isEmpty()).isFalse();
        inactiveUserCleaner.cleanInactiveUsers();
        assertThat(todoItemRepository.findByUserId(user.id).isEmpty()).isTrue();
    }

    @Test
    public void thatUsefulLinksAreDeleted() {
        User user = getUserToBeDeleted();

        assertThat(usefulLinkRepository.findByUserIdOrderByOrderIndexAsc(user.id).isEmpty()).isFalse();
        inactiveUserCleaner.cleanInactiveUsers();
        assertThat(usefulLinkRepository.findByUserIdOrderByOrderIndexAsc(user.id).isEmpty()).isTrue();
    }

    @Test
    public void thatFavoritesAreDeleted() {
        User user = getUserToBeDeleted();

        assertThat(favoriteRepository.findByUserIdOrderByOrderIndexAsc(user.id).isEmpty()).isFalse();
        inactiveUserCleaner.cleanInactiveUsers();
        assertThat(favoriteRepository.findByUserIdOrderByOrderIndexAsc(user.id).isEmpty()).isTrue();
    }

    @Test
    public void thatOfficeHoursAreDeleted() {
        User user = getUserToBeDeleted();

        assertThat(officeHoursRepository.findByUserId(user.id).isEmpty()).isFalse();
        inactiveUserCleaner.cleanInactiveUsers();
        assertThat(officeHoursRepository.findByUserId(user.id).isEmpty()).isTrue();
    }

    @Test
    public void thatOnlyInactiveUsersWithoutProfileAreDeletedFromUserAccountTable() {
        long userId = createUser(null);
        createProfile(userId);

        inactiveUserCleaner.cleanInactiveUsers();

        assertThat(userRepository.getOne(userId)).isNotNull();
    }

    @Test
    public void thatUploadedImageIsDeleted() {
        FileStorage fileStorage = mock(MemoryFileStorage.class);

        new InactiveUserCleaner(
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

    @Test
    public void thatActiveUntilFieldIsUpdatedIfAccountIsActive() {
        long userId = createUser(null);

        inactiveUserCleaner.cleanInactiveUsers();

        DateTime accountActiveUntilDate = userRepository
            .findById(userId)
            .orElseThrow(IllegalStateException::new)
            .accountActiveUntilDate;

        assertThat(accountActiveUntilDate).isNotNull();
    }

    @Test
    public void thatOnlyInactiveUsersAreFetchedFromDB() {
        long activeUserId = createUser(DateTime.now().plusDays(1));

        List<User> inactiveUsers = inactiveUserCleaner.findInactiveUsers();
        List<Long> userIds = inactiveUsers.stream().map(u -> u.id).collect(Collectors.toList());

        assertThat(inactiveUsers.size()).isEqualTo(1);
        assertThat(userIds).contains(INACTIVE_USER_ID);
        assertThat(userIds).doesNotContain(activeUserId);
    }

    @Test(expected = IllegalStateException.class)
    public void thatWorksWhenUserAccountIsNotFoundFromIAM() {
        long userId = createUser(DateTime.now().minusYears(1), "notfound@helsinki.fi");
        User user = userRepository.getOne(userId);

        inactiveUserCleaner.fetchAccountStatus(user);
    }

    private long createUser(DateTime accountActiveUntilDate) {
        return createUser(accountActiveUntilDate, "inactive@helsinki.fi");
    }

    private long createUser(DateTime accountActiveUntilDate, String eppn) {
        User user = new User();

        user.eduPersonPrincipalName = eppn;
        user.oodiPersonId = "805";
        user.accountStatus = User.AccountStatus.ACTIVE;
        user.lastLoginDate = DateTime.now().minusYears(1);
        user.accountActiveUntilDate = accountActiveUntilDate;

        return userRepository.save(user).id;
    }

    private void createProfile(long userId) {
        Profile profile = new Profile();

        profile.user = userRepository.getOne(userId);
        profile.language = Language.EN;
        profile.path = "foo";
        profile.visibility = ProfileVisibility.PUBLIC;
        profile.ownerName = "owner";
        profile.profileRole = ProfileRole.STUDENT;

        profileRepository.save(profile);
    }
}
