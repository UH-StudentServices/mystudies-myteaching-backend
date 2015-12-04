package fi.helsinki.opintoni.service.converter;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import fi.helsinki.opintoni.dto.FeedDto;
import fi.helsinki.opintoni.dto.FeedEntryDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FeedConverter {

    private LocalDateTime dateToLocalDateTime(Date date) {
        return Optional.ofNullable(date)
            .map(d -> LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()))
            .orElseGet(() -> LocalDateTime.now());
    }

    private List<FeedEntryDto> getEntries(SyndFeed syndFeed, int limit) {
        if(syndFeed.getEntries() == null) {
            return null;
        } else {
            return syndFeed.getEntries().stream()
                .limit(limit)
                .map(this::toFeedEntryDto)
                .collect(Collectors.toList());
        }
    }

    private FeedEntryDto toFeedEntryDto(SyndEntry syndEntry) {
        FeedEntryDto feedEntryDto = new FeedEntryDto();
        feedEntryDto.title = syndEntry.getTitle();
        feedEntryDto.link = syndEntry.getLink();
        feedEntryDto.date = dateToLocalDateTime(syndEntry.getPublishedDate());
        feedEntryDto.description = Optional.ofNullable(syndEntry.getDescription())
            .map(e -> e.getValue())
            .orElse(null);

        return feedEntryDto;
    }

    public FeedDto toDto(SyndFeed feed, int limit) {
        FeedDto feedDto = new FeedDto();
        feedDto.title = feed.getTitle();
        feedDto.link = feed.getLink();
        feedDto.date = dateToLocalDateTime(feed.getPublishedDate());
        feedDto.entries = getEntries(feed, limit);

        return feedDto;
    }

}
