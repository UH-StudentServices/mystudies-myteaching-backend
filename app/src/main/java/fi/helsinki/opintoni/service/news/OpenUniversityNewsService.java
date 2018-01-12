package fi.helsinki.opintoni.service.news;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.dto.NewsDto;
import fi.helsinki.opintoni.integration.publicwww.PublicWwwRestClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OpenUniversityNewsService extends FetchingNewsService {

    @Autowired
    private PublicWwwRestClient publicWwwRestClient;

    @Cacheable(value = CacheConstants.OPEN_UNIVERSITY_NEWS, cacheManager = "transientCacheManager")
    public List<NewsDto> getOpenUniversityNews() {
        return getRssNews(publicWwwRestClient::getOpenUniversityFeed);
    }

}
