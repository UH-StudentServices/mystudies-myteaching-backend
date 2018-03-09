/*
 * This file is part of MystudiesMyteaching application.
 *
 * MystudiesMyteaching application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MystudiesMyteaching application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MystudiesMyteaching application.  If not, see <http://www.gnu.org/licenses/>.
 */

package fi.helsinki.opintoni.service.news;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.dto.NewsDto;
import fi.helsinki.opintoni.integration.newsfeeds.GuideNewsClient;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiStudyRight.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GuideNewsService extends FetchingNewsService {

    @Autowired
    private OodiClient oodiClient;

    @Autowired
    private GuideNewsClient guideNewsClient;

    @Cacheable(value = CacheConstants.GUIDE_GENERAL_NEWS, cacheManager = "transientCacheManager")
    public List<NewsDto> getGuideNewsGeneral(Locale locale) {
        return getAtomNews(() -> guideNewsClient.getGuideFeed(locale));
    }

    @Cacheable(value = CacheConstants.GUIDE_PROGRAMME_NEWS, cacheManager = "transientCacheManager")
    public List<NewsDto> getGuideNewsForDegreeProgramme(String studentNumber, Locale locale) {

        Set<String> studyRightsProgrammeCodes = oodiClient.getStudentStudyRights(studentNumber).stream()
            .flatMap(osr -> osr.elements.stream())
            .filter(GuideNewsService::elementMatchesProgramme)
            .map(e -> e.code).collect(Collectors.toSet());

        HashSet<NewsDto> newsSet = studyRightsProgrammeCodes.stream().map(code -> getAtomNews(
            () -> guideNewsClient.getGuideFeed(locale, code)))
            .flatMap(List::stream)
            .collect(Collectors.toCollection(HashSet::new));

        return new ArrayList<>(newsSet);
    }

    private static boolean elementMatchesProgramme(Element element) {
        return element.id.equals(GuideNewsConstants.OODI_STUDY_RIGHTS_DEGREE_PROGRAMME_ID) &&
            (element.code.toUpperCase()
                    .startsWith(GuideNewsConstants.OODI_STUDY_RIGHTS_BACHELOR_PROGRAMME_CODE_PREFIX) ||
                element.code.toUpperCase()
                    .startsWith(GuideNewsConstants.OODI_STUDY_RIGHTS_MASTERS_PROGRAMME_CODE_PREFIX)
            );
    }
}
