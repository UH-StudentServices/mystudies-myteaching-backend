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

import fi.helsinki.opintoni.domain.UsefulLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsefulLinkRepository extends JpaRepository<UsefulLink, Long> {
    List<UsefulLink> findByUserIdOrderByOrderIndexAsc(Long userId);

    @Query("select coalesce(max(f.orderIndex), 0) from #{#entityName} f where f.user.id = ?1")
    int getMaxOrderIndex(Long userId);

    long deleteByUserId(Long userId);
}
