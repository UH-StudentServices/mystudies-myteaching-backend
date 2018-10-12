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

package fi.helsinki.opintoni.security.authorization;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.Favorite;
import fi.helsinki.opintoni.exception.http.ForbiddenException;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class PermissionCheckerTest extends SpringTest {

    @Autowired
    private PermissionChecker permissionChecker;

    @Test(expected = ForbiddenException.class)
    public void thatForbiddenIsThrownWhenVerifyingPermissionForEntity() {
        permissionChecker.verifyPermission(2L, 1L, Favorite.class);
    }

    @Test(expected = ForbiddenException.class)
    public void thatForbiddenIsThrownWhenVerifyingPermissionForEntities() {
        permissionChecker.verifyPermission(2L, Lists.newArrayList(1L, 2L), Favorite.class);
    }

    @Test(expected = NotFoundException.class)
    public void thatNotFoundIsThrownWhenVerifyingPermission() {
        permissionChecker.verifyPermission(1L, 0L, Favorite.class);
    }

    @Test
    public void thatPermissionIsVerifiedForEntity() {
        permissionChecker.verifyPermission(1L, 1L, Favorite.class);
    }

    @Test
    public void thatPermissionIsVerifiedForEntities() {
        permissionChecker.verifyPermission(1L, Lists.newArrayList(1L, 3L), Favorite.class);
    }

    @Test
    public void thatUserDoesNotHavePermission() {
        assertThat(permissionChecker.hasPermission(2L, 1L, Favorite.class)).isFalse();
    }

    @Test(expected = NotFoundException.class)
    public void thatNotFoundIsThrownWhenCheckingPermission() {
        permissionChecker.hasPermission(1L, 0L, Favorite.class);
    }

    @Test
    public void thatUserHasPermission() {
        assertThat(permissionChecker.hasPermission(1L, 1L, Favorite.class)).isTrue();
    }

}
