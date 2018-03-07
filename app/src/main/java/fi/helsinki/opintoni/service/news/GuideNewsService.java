package fi.helsinki.opintoni.service.news;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.dto.NewsDto;
import fi.helsinki.opintoni.exception.http.RestClientServiceException;
import fi.helsinki.opintoni.integration.newsfeeds.GuideNewsClient;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiIntegrationException;
import fi.helsinki.opintoni.integration.oodi.OodiStudyRight.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GuideNewsService extends FetchingNewsService {

    private static final Logger logger = LoggerFactory.getLogger(GuideNewsService.class);

    private final OodiClient oodiClient;

    private final GuideNewsClient guideNewsClient;

    @Autowired
    public GuideNewsService(OodiClient oodiClient, GuideNewsClient guideNewsClient) {
        this.oodiClient = oodiClient;
        this.guideNewsClient = guideNewsClient;
    }

    @Cacheable(value = CacheConstants.GUIDE_GENERAL_NEWS, cacheManager = "transientCacheManager")
    public List<NewsDto> getGuideNewsGeneral(Locale locale) {
        return getAtomNews(() -> guideNewsClient.getGuideFeed(locale));
    }

    @Cacheable(value = CacheConstants.GUIDE_PROGRAMME_NEWS, cacheManager = "transientCacheManager")
    public List<NewsDto> getGuideNewsForDegreeProgramme(String studentNumber, Locale locale) {
        try {
            Set<String> studyRightsProgrammeCodes = oodiClient.getStudentStudyRights(studentNumber).stream()
                .flatMap(osr -> osr.elements.stream())
                .filter(GuideNewsService::elementMatchesProgramme)
                .map(e -> e.code).collect(Collectors.toSet());

            return studyRightsProgrammeCodes.stream()
                .map(code -> getAtomNews(() -> guideNewsClient.getGuideFeed(locale, code)))
                .flatMap(List::stream).distinct().collect(Collectors.toList());
        } catch (OodiIntegrationException | RestClientServiceException e) {
            logger.warn("Failed to fetch guide news for degree program(s)");
            return new ArrayList<>();
        }
    }

    private static boolean elementMatchesProgramme(Element element) {
        return element.id.equals(GuideNewsConstants.OODI_STUDY_RIGHTS_DEGREE_PROGRAMME_ID) && (
                element.code.toUpperCase()
                    .startsWith(GuideNewsConstants.OODI_STUDY_RIGHTS_BACHELOR_PROGRAMME_CODE_PREFIX) ||
                element.code.toUpperCase()
                    .startsWith(GuideNewsConstants.OODI_STUDY_RIGHTS_MASTERS_PROGRAMME_CODE_PREFIX)
        );
    }


}
