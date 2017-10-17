package fi.helsinki.opintoni.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.Test;


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
        Assertions.assertThat( set.stream().anyMatch(d -> d.title.equals("titleon3"))).isTrue();
    }

    public static NewsDto createDto(String title, Date updated, String content, String url) {
        NewsDto dto = new NewsDto();
        dto.title = title;
        dto.updated = updated;
        dto.content = content ;
        dto.url = url;
        return dto;
    }

    public static NewsDto copyDto(NewsDto dto) {
        return createDto(dto.title, dto.updated, dto.content, dto.url);
    }
}
