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

import java.util.*;

public class NewsDto {

    public String title;
    public String url;
    public String content;
    public Date updated;
    public Set<String> categories = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof NewsDto)) {
            return false;
        }

        NewsDto dto = (NewsDto) o;

        return Objects.equals(title, dto.title)
            && Objects.equals(url, dto.url)
            && Objects.equals(content, dto.content)
            && Objects.equals(updated, dto.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, url, content, updated);
    }
}
