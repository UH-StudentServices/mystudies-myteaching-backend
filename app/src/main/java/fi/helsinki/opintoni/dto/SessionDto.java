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

package fi.helsinki.opintoni.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SessionDto {

    public String username;
    public String name;
    public String email;
    public String avatarUrl;
    public FacultyDto faculty;
    public Map<String, Map<String, List<String>>> portfolioPathsByRoleAndLang;
    public Set<String> roles;
    public boolean openUniversity;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("username", username)
            .append("name", name)
            .append("email", email)
            .append("avatarUrl", avatarUrl)
            .append("roles", roles)
            .toString();
    }
}
