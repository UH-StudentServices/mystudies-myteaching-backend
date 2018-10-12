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

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class NewsDtoTest {

    @Test
    public void thatNewsDtosHashProperly() {

        NewsDto dto1 = createDto("title", new Date(), "content", "urli");
        NewsDto dto2 = copyDto(dto1);

        NewsDto dto3 = copyDto(dto1);
        dto3.title = dto1.title + "on3";

        Set<NewsDto> set = new HashSet<>();
        set.add(dto1);
        set.add(dto2);
        set.add(dto3);

        Assertions.assertThat(set.size()).isEqualTo(2);
        Assertions.assertThat(set.stream().anyMatch(d -> d.title.equals("titleon3"))).isTrue();
    }

    public static NewsDto createDto(String title, Date updated, String content, String url) {
        NewsDto dto = new NewsDto();
        dto.title = title;
        dto.updated = updated;
        dto.content = content;
        dto.url = url;
        return dto;
    }

    public static NewsDto copyDto(NewsDto dto) {
        return createDto(dto.title, dto.updated, dto.content, dto.url);
    }
}
