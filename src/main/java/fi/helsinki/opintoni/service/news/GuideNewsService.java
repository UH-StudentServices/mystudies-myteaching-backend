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
import fi.helsinki.opintoni.exception.http.RestClientServiceException;
import fi.helsinki.opintoni.integration.newsfeeds.GuideNewsClient;
import fi.helsinki.opintoni.integration.oodi.OodiClient;
import fi.helsinki.opintoni.integration.oodi.OodiIntegrationException;
import fi.helsinki.opintoni.integration.oodi.OodiStudyRight;
import fi.helsinki.opintoni.integration.oodi.OodiStudyRight.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<NewsDto> getGuideNewsForStudent(String studentNumber, Locale locale) {
        try {
            List<String> codes = oodiClient.getStudentStudyRights(studentNumber).stream()
                    .flatMap(sr -> getStudyRightElementCodes(sr).stream())
                    .collect(Collectors.toList());

            return getAtomNews(() -> guideNewsClient.getGuideFeed(locale, codes));
        } catch (OodiIntegrationException | RestClientServiceException e) {
            logger.warn("Failed to fetch guide news for degree program(s)");
            return new ArrayList<>();
        }
    }

    // Given a study right return a list of codes for all degree programmes and all degree programme - major combinations:
    // dp1, dp2, dp1+major1, dp1+major2, dp2+major1, dp2+major2, ...
    // We do not drop out elements whose end_date is in the past even when there exists a newer element with same id.
    // So the user may see news related to a previous study right.
    protected List<String> getStudyRightElementCodes(OodiStudyRight studyRight) {
        List<String> dpCodes = studyRight.elements.stream()
                .filter(GuideNewsService::elementMatchesProgramme)
                .map(e -> e.code)
                .collect(Collectors.toList());

        return Stream.concat(
                studyRight.elements.stream()
                        .filter(GuideNewsService::elementMatchesMajor)
                        .map(e -> e.code)
                        .flatMap(m -> dpCodes.stream().map(dp -> dp + m)),
                dpCodes.stream()).collect(Collectors.toList());
    }

    private static boolean elementMatchesProgramme(Element element) {
        return element.id.equals(GuideNewsConstants.OODI_STUDY_RIGHTS_DEGREE_PROGRAMME_ID) &&
                (element.code.toUpperCase()
                        .startsWith(GuideNewsConstants.OODI_STUDY_RIGHTS_BACHELOR_PROGRAMME_CODE_PREFIX) ||
                        element.code.toUpperCase()
                                .startsWith(GuideNewsConstants.OODI_STUDY_RIGHTS_MASTERS_PROGRAMME_CODE_PREFIX)
                );
    }

    private static boolean elementMatchesMajor(Element element) {
        return element.id.equals(GuideNewsConstants.OODI_STUDY_RIGHTS_MAJOR_ID);
    }
}
