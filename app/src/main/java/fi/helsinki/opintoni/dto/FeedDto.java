package fi.helsinki.opintoni.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FeedDto {
    public String title;
    public String link;
    public LocalDateTime date;
    public List<FeedEntryDto> entries;
}
