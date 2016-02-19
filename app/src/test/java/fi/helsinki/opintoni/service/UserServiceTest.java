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

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.Favorite;
import fi.helsinki.opintoni.domain.UsefulLink;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.domain.UserSettings;
import fi.helsinki.opintoni.repository.FavoriteRepository;
import fi.helsinki.opintoni.repository.UsefulLinkRepository;
import fi.helsinki.opintoni.repository.UserSettingsRepository;
import fi.helsinki.opintoni.security.AppUser;
import fi.helsinki.opintoni.security.enumerated.SAMLEduPersonAffiliation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class UserServiceTest extends SpringTest {

    private static final String EDU_PERSON_PRINCIPAL_NAME = "test@helsinki.fi";

    @Autowired
    private UserService userService;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UsefulLinkRepository usefulLinkRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Test
    public void thatUserIsFoundByEduPersonPrincipalName() {
        Optional<User> user = userService.findFirstByEduPersonPrincipalName(EDU_PERSON_PRINCIPAL_NAME);
        assertThat(user.isPresent()).isTrue();
    }

    @Test
    public void thatUserIsSaved() {
        User user = saveStudent();
        assertThat(user.id).isNotNull();
    }

    @Test
    public void thatSettingsAreCreated() {
        User user = saveStudent();
        UserSettings userSettings = userSettingsRepository.findByUserId(user.id);
        userSettings.backgroundFilename = "Profile_1.jpg";
    }

    @Test
    public void thatUsefulLinksAreCreated() {
        User user = saveStudent();
        List<UsefulLink> usefulLinks = usefulLinkRepository.findByUserIdOrderByOrderIndexAsc(user.id);
        assertThat(usefulLinks.size() > 0).isTrue();
    }

    @Test
    public void thatFavoritesAreCreated() {
        User user = saveStudent();
        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByOrderIndexAsc(user.id);
        assertThat(favorites.size() > 0).isTrue();
    }

    private User saveStudent() {
        studentRequestChain("123")
            .enrollments()
            .studyRights();
        return userService.createNewUser(createAppUser());
    }

    private AppUser createAppUser() {
        return new AppUser.AppUserBuilder()
            .oodiPersonId("111")
            .eduPersonPrincipalName("newUser")
            .studentNumber("123")
            .eduPersonAffiliations(Arrays.asList(SAMLEduPersonAffiliation.STUDENT))
            .build();
    }

    @Test
    public void thatUserIsAdmin() {
        assertThat(userService.isAdmin("opettaja@helsinki.fi")).isTrue();
    }

    @Test
    public void thatUserIsNotAdmin() {
        assertThat(userService.isAdmin("notadmin@helsinki.fi")).isFalse();
    }
}
