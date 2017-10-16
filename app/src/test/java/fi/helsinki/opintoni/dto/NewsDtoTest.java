package fi.helsinki.opintoni.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class NewsDtoTest {


    @Test
    public void thatNewsDtosHashProperly() {

        NewsDto dto1 = new NewsDto();
        dto1.title="title";
        dto1.updated = new Date();
        dto1.content="content";
        dto1.url="urli";

        NewsDto dto2 = new NewsDto();
        dto2.title = dto1.title;
        dto2.updated = dto1.updated;
        dto2.content = dto1.content;
        dto2.url = dto1.url;

        NewsDto dto3 = new NewsDto();
        dto3.title = dto1.title + "on3";
        dto3.updated = dto1.updated;
        dto3.content = dto1.content;
        dto3.url = dto1.url;

        Set<NewsDto> set = new HashSet<>();
        set.add(dto1);
        set.add(dto2);
        set.add(dto3);

        Assert.assertEquals(2, set.size());
        Assert.assertTrue("dto3 is in set", set.stream().anyMatch(d -> d.title.equals("titleon3")));

    }
}
