package fi.helsinki.opintoni.service.news;

import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.rss.Channel;
import fi.helsinki.opintoni.dto.NewsDto;
import fi.helsinki.opintoni.service.converter.NewsConverter;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FetchingNewsService {

    @Autowired
    protected NewsConverter newsConverter;

    @Value("${newsfeeds.maxItemsToReturn}")
    protected int maxNews;

    protected List<NewsDto> getAtomNews(Supplier<Feed> feedSupplier) {
        return feedSupplier.get().getEntries().stream()
            .limit(maxNews)
            .map(newsConverter::toDtoFromAtom)
            .collect(Collectors.toList());
    }

    protected List<NewsDto> getRssNews(Supplier<Channel> channelSupplier) {
        return channelSupplier.get().getItems().stream()
            .limit(maxNews)
            .map(newsConverter::toDtoFromRss)
            .collect(Collectors.toList());
    }

}
