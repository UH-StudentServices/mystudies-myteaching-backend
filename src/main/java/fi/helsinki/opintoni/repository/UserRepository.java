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

package fi.helsinki.opintoni.repository;

import fi.helsinki.opintoni.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEduPersonPrincipalName(String eduPersonPrincipalName);

    Optional<User> findByOodiPersonId(String oodiPersonId);

    @Query(
        value = "select * from user_account u where u.last_login_date < now() - interval '1 year' " +
        "and u.account_status = 'ACTIVE' " +
        "and (u.account_active_until_date is null or u.account_active_until_date < now())", nativeQuery = true)
    List<User> findInactiveUsers();

}
