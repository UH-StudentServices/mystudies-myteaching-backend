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

package fi.helsinki.opintoni.integration.sotka;

import java.util.List;
import java.util.Optional;

import fi.helsinki.opintoni.integration.sotka.model.SotkaHierarchy;

public interface SotkaClient {

    Optional<SotkaHierarchy> getOptimeHierarchy(String optimeId);

    /**
     * Returns list of sotka hierarchies containing oodi and optime ids for given input list of sisu realisation ids.
     * NOTE: Not yet usable as corresponding api endpoint has not been implemented on sisu side yet
     *
     * @param optimeIds List of sisu realisation ids
     * @return List of sotka hierarchies
     */
    List<SotkaHierarchy> getOptimeHierarchy(List<String> optimeIds);
}
