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

import fi.helsinki.opintoni.domain.profile.ComponentVisibility;
import fi.helsinki.opintoni.domain.profile.ProfileComponent;
import fi.helsinki.opintoni.domain.profile.TeacherProfileSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComponentVisibilityRepository extends JpaRepository<ComponentVisibility, Long> {

    List<ComponentVisibility> findByProfileId(Long profileId);

    Optional<ComponentVisibility> findByProfileIdAndComponent(Long profileId,
                                                              ProfileComponent component);

    Optional<ComponentVisibility> findByProfileIdAndComponentAndTeacherProfileSectionAndInstanceName(
        Long profileId,
        ProfileComponent component,
        TeacherProfileSection teacherProfileSection,
        String instanceName);

    Long deleteByProfileIdAndComponentAndInstanceName(Long profileId,
                                                      ProfileComponent component,
                                                      String instanceName);
}
