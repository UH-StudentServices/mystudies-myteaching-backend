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

package fi.helsinki.opintoni.integration.studies;

import fi.helsinki.opintoni.SpringTest;
import org.apache.commons.collections4.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class StudiesRestClientTest extends SpringTest {
    private static final Locale LOCALE_FI = new Locale("fi");

    private StudiesClient studiesClient;
    private RestTemplate spyRestTemplate;
    private String studiesBaseUrl;

    @Before
    public void init() {
        spyRestTemplate = Mockito.spy(studiesRestTemplate);
        this.studiesClient = new StudiesRestClient(spyRestTemplate, appConfiguration.get("studies.client.api.url"));
        this.studiesBaseUrl = appConfiguration.get("studies.base.url");
    }

    @Test
    public void thatCoursePageUrlsAreReturnedForCourseIds() throws Exception {
        List<String> courseIds = List.of("cur-1", "cur-2");
        studiesServer.expectCoursePageUrlsRequest(courseIds, LOCALE_FI);

        Map<String, String> coursePageUrls = studiesClient.getCoursePageUrls(courseIds, LOCALE_FI);
        assertThat(coursePageUrls.size()).isEqualTo(2);
        courseIds.forEach(courseId -> assertThat(coursePageUrls.get(courseId)).isEqualTo(studiesBaseUrl + "/opintotarjonta/cur/" + courseId));
    }

    @Test
    public void thatEmptyMapIsReturnedWhenCoursePageUrlsRequestFails() {
        doThrow(new RuntimeException())
            .when(spyRestTemplate)
            .exchange(any(), eq(HttpMethod.GET), isNull(), eq(new ParameterizedTypeReference<Map<String, String>>() {}));

        Map<String, String> coursePageUrls = studiesClient.getCoursePageUrls(List.of("cur-1"), LOCALE_FI);
        assertThat(coursePageUrls.isEmpty()).isTrue();
    }

    @Test
    public void thatEmptyMapIsReturnedWhenCoursePageUrlsIsCalledWithoutCourseIds() {
        Map<String, String> coursePageUrls = studiesClient.getCoursePageUrls(Collections.emptyList(), LOCALE_FI);

        verifyZeroInteractions(spyRestTemplate);

        assertThat(coursePageUrls.isEmpty()).isTrue();
    }

    @Test
    public void thatEmptyMapIsReturnedWhenCoursePageUrlsIsCalledWithNullAsCourseIds() {
        Map<String, String> coursePageUrls = studiesClient.getCoursePageUrls(null, LOCALE_FI);

        verifyZeroInteractions(spyRestTemplate);

        assertThat(coursePageUrls.isEmpty()).isTrue();
    }

    @Test
    public void thatFetchingCoursePageUrlsWithOver100IdsIsPartitionedToSeparateApiCalls() {
        List<String> ids = IntStream.range(1, 999).mapToObj(String::valueOf).collect(Collectors.toUnmodifiableList());
        ListUtils.partition(ids, 100).forEach(idListPartition -> {
            try {
                studiesServer.expectCoursePageUrlsRequest(idListPartition, LOCALE_FI);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Map<String, String> coursePageUrls = studiesClient.getCoursePageUrls(ids, LOCALE_FI);

        verify(spyRestTemplate, times(10))
            .exchange(any(), eq(HttpMethod.GET), isNull(), eq(new ParameterizedTypeReference<Map<String, String>>() {}));

        assertThat(coursePageUrls.size()).isEqualTo(ids.size());
    }
}
