package fi.helsinki.opintoni.service.news;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.dto.NewsDto;
import fi.helsinki.opintoni.integration.newsfeeds.FlammaRestClient;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class FlammaNewsService extends FetchingNewsService {

    @Autowired
    private FlammaRestClient flammaRestClient;

    @Cacheable(CacheConstants.STUDENT_NEWS)
    public List<NewsDto> getStudentNews(Locale locale) {
        return getAtomNews(() -> flammaRestClient.getStudentFeed(locale));
    }

    @Cacheable(CacheConstants.TEACHER_NEWS)
    public List<NewsDto> getTeacherNews(Locale locale) {
        return getAtomNews(() -> flammaRestClient.getTeacherFeed(locale));
    }


}
