package fi.helsinki.opintoni.service.news;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.dto.NewsDto;
import fi.helsinki.opintoni.integration.newsfeeds.FlammaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class FlammaNewsService extends FetchingNewsService {

    @Autowired
    private FlammaClient flammaClient;

    @Cacheable(value = CacheConstants.STUDENT_NEWS, cacheManager = "inMemoryCacheManager")
    public List<NewsDto> getStudentNews(Locale locale) {
        return getAtomNews(() -> flammaClient.getStudentFeed(locale));
    }

    @Cacheable(value = CacheConstants.TEACHER_NEWS, cacheManager = "inMemoryCacheManager")
    public List<NewsDto> getTeacherNews(Locale locale) {
        return getAtomNews(() -> flammaClient.getTeacherFeed(locale));
    }


}
