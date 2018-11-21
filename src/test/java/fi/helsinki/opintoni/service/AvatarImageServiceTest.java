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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class AvatarImageServiceTest extends SpringTest {

    @Autowired
    AvatarImageService avatarImageService;

    @Test
    public void thatDefaultAvatarImageUrlIsReturned() {
        assertThat(avatarImageService.getAvatarImageUrl(1L)).isEqualTo("https://dev.student.helsinki.fi/assets/icons/avatar.png");
    }

    @Test
    public void thatDefaultProfileAvatarImageUrlIsReturned() {
        assertThat(avatarImageService.getProfileAvatarImageUrl(1L)).isEqualTo("/profile/assets/icons/avatar.png");
    }

    @Test
    public void thatAvatarImageUrlIsReturned() {
        assertThat(avatarImageService.getAvatarImageUrl(2L)).isEqualTo("https://dev.student.helsinki.fi/api/public/v1/images/avatar/200");
    }

    @Test
    public void thatProfileAvatarImageUrlIsReturned() {
        assertThat(avatarImageService.getProfileAvatarImageUrl(2L)).isEqualTo("https://dev.student.helsinki.fi/api/public/v1/images/avatar/200");
    }

}
