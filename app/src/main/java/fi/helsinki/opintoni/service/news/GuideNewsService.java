package fi.helsinki.opintoni.service.news;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.dto.NewsDto;
import fi.helsinki.opintoni.integration.newsfeeds.GuideNewsRestClient;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiStudyRight.Element;
import fi.helsinki.opintoni.security.SecurityUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class GuideNewsService extends FetchingNewsService {

    @Autowired
    private OodiClient oodiClient;

    @Autowired
    private GuideNewsRestClient guideNewsRestClient;

    @Autowired
    private SecurityUtils securityUtils;

    @Cacheable(CacheConstants.GUIDE_GENERAL_NEWS)
    public List<NewsDto> getGuideNewsGeneral(Locale locale) {
        return getAtomNews(() -> guideNewsRestClient.getGuideFeed(locale));
    }

    @Cacheable(CacheConstants.GUIDE_PROGRAMME_NEWS)
    public List<NewsDto> getGuideNewsForDegreeProgramme(String studentNumber, Locale locale) {

        Set<String> studyRightsProgrammeCodes = oodiClient.getStudentStudyRights(studentNumber).stream()
            .map(osr -> osr.elements)
            .flatMap(Collection::stream)
            .filter(GuideNewsService::elementMatchesProgramme)
            .map(e -> e.code).collect(Collectors.toSet());

        HashSet<NewsDto> newsSet = studyRightsProgrammeCodes.stream().map(code -> getAtomNews(
            () -> guideNewsRestClient.getGuideFeed(locale, code)))
            .flatMap(List::stream)
            .collect(Collectors.toCollection(HashSet::new));

        return new ArrayList<>(newsSet);
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
