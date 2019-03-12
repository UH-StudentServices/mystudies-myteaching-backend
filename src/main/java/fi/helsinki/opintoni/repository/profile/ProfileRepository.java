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

package fi.helsinki.opintoni.repository.profile;

import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.web.arguments.ProfileRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.stream.Stream;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    int countByPath(String path);

    Stream<Profile> findByUserIdAndProfileRole(Long userId,
                                               ProfileRole profileRole);

    Optional<Profile> findByPathAndProfileRoleAndLanguage(String path,
                                                          ProfileRole profileRole,
                                                          Language language);

    Stream<Profile> findByUserId(Long userId);

    // Optional<byte[]> does not work here, as it will return an Optional<ArrayList<byte[]>>
    @Query(value = "select u.avatar_image from profile p, user_settings u where p.path = :path and u.user_id = p.user_id limit 1",
        nativeQuery = true)
    byte[] getProfileImageByByPath(@Param("path") String path);

    @Query(value = "select u.avatar_image from profile p, user_settings u, profile_shared_link s where " +
        "s.shared_path_fragment = :sharedLinkFragment and s.profile_id = p.id and u.user_id = p.user_id",
        nativeQuery = true)
    byte[] getProfileImageBySharedLinkFragment(@Param("sharedLinkFragment") String sharedLinkFragment);
}
