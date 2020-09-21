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

package fi.helsinki.opintoni.integration.studyregistry.sisu;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import fi.helsinki.opintoni.cache.CacheConstants;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.AcceptorPersonResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.AddressResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.AttainmentResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Authenticated_course_unit_realisation_searchQueryRequest;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Authenticated_course_unit_realisation_searchQueryResponse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.BuildingResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitRealisationOrganisationResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitRealisationResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.CourseUnitResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.DatePeriodInputTO;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.DatePeriodResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.EventOverrideResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.LocalizedStringResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.LocationResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.OrganisationResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.PrivatePersonResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Private_personQueryRequest;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Private_personQueryResponse;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.Private_personsQueryRequest;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.PublicPersonResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyEventRealisationResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyEventResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudyGroupSetResponseProjection;
import fi.helsinki.opintoni.integration.studyregistry.sisu.model.StudySubGroupResponseProjection;
import fi.helsinki.opintoni.util.DateTimeUtil;

public class SisuGraphQLClient implements SisuClient {

    private final RestTemplate restTemplate;
    private final String url;

    public SisuGraphQLClient(String url, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    private <T> T execute(GraphQLRequest request, Class<T> type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(request.toHttpJsonBody(), headers);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, type).getBody();
    }

    private static final LocalizedStringResponseProjection ALL_LANGUAGES = new LocalizedStringResponseProjection().en().fi().sv();

    @Override
    @Cacheable(value = CacheConstants.GRAPHQL_CUR_SEARCH, cacheManager = "transientCacheManager", sync = true)
    public  Authenticated_course_unit_realisation_searchQueryResponse curSearch(String responsiblePerson, LocalDate since) {
        GraphQLOperationRequest qr = getTeacherCoursesRequest(responsiblePerson, since);

        CourseUnitRealisationResponseProjection curp = new CourseUnitRealisationResponseProjection()
            .name(ALL_LANGUAGES)
            .id()
            .organisations(new CourseUnitRealisationOrganisationResponseProjection()
                .organisation(new OrganisationResponseProjection().code().name(ALL_LANGUAGES)))
            .courseUnits(new CourseUnitResponseProjection().code())
            .flowState()
            .courseUnitRealisationTypeUrn()
            .activityPeriod(new DatePeriodResponseProjection().startDate().endDate())
            .studyGroupSets(
                new StudyGroupSetResponseProjection()
                .name(ALL_LANGUAGES)
                .studySubGroups(new StudySubGroupResponseProjection()
                    .name(ALL_LANGUAGES)
                    .teacherIds()
                    .studyEvents(
                        new StudyEventResponseProjection()
                            .overrides(new EventOverrideResponseProjection()
                                .notice(ALL_LANGUAGES)
                                .eventDate()
                                .irregularLocations(new LocationResponseProjection()
                                    .name(ALL_LANGUAGES).building(new BuildingResponseProjection()
                                        .address(new AddressResponseProjection().postalCode().streetAddress())
                                    )
                                )
                            )
                            .locations(new LocationResponseProjection()
                                .name(ALL_LANGUAGES)
                                .building(
                                    new BuildingResponseProjection()
                                        .address(new AddressResponseProjection().streetAddress())
                                        .name(ALL_LANGUAGES)
                                )
                            )
                            .events(
                                new StudyEventRealisationResponseProjection()
                                    .end()
                                    .start()
                                    .cancelled()
                                    .excluded()))));

        GraphQLRequest gqlRequest = new GraphQLRequest(qr, curp);

        return execute(gqlRequest, Authenticated_course_unit_realisation_searchQueryResponse.class);
    }

    private GraphQLOperationRequest getTeacherCoursesRequest(String id, LocalDate since) {
        String start = DateTimeUtil.getSemesterStartDateSisuString(since);
        String end = DateTimeUtil.getSemesterStartDateSisuString(since.plusYears(1));

        return new Authenticated_course_unit_realisation_searchQueryRequest.Builder()
            .setResponsiblePersonIds(Arrays.asList(id))
            .setActivityPeriods(Arrays.asList(new DatePeriodInputTO(end, start)))
            .setLimit(10000d)
            .build();
    }

    // TODO cache
    @Override
    public Private_personQueryResponse getPrivatePerson(String personId) {
        GraphQLOperationRequest qr = new Private_personsQueryRequest.Builder().setIds(List.of(personId)).build();
        PrivatePersonResponseProjection projection =  new PrivatePersonResponseProjection()
            .studentNumber()
            .employeeNumber();
        GraphQLRequest gqlRequest = new GraphQLRequest(qr, projection);
        return execute(gqlRequest, Private_personQueryResponse.class);
    }

    // TODO cache
    @Override
    public Private_personQueryResponse getStudyAttainments(String personId) {
        GraphQLOperationRequest qr = new Private_personQueryRequest.Builder().setId(personId).build();
        PrivatePersonResponseProjection projection =  new PrivatePersonResponseProjection()
            .studentNumber()
            .employeeNumber()
            .attainments(new AttainmentResponseProjection()
                .acceptorPersons(new AcceptorPersonResponseProjection()
                .person(new PublicPersonResponseProjection()
                    .firstName().lastName())));
        GraphQLRequest gqlRequest = new GraphQLRequest(qr, projection);
        return execute(gqlRequest, Private_personQueryResponse.class);
    }

}
