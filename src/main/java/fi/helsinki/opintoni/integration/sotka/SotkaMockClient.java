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

package fi.helsinki.opintoni.integration.sotka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import fi.helsinki.opintoni.integration.sotka.model.SotkaHierarchy;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class SotkaMockClient implements SotkaClient {

    private static final String TEST_REALISATION_SISU_CUR_ID = "hy-opt-cur-2021-e1f67d07-d60f-4cbd-ab85-6b6bfecd7cf4";
    private static final String TEST_REALISATION_NOT_FOUND_SISU_CUR_ID = "hy-cur-123456789";

    private final ObjectMapper objectMapper;

    @Value("classpath:sampledata/sotka/oodi_hierarchy.json")
    private Resource hierarchy1;

    @Value("classpath:sampledata/sotka/oodi_hierarchy_from_optime.json")
    private Resource hierarchy2;

    public SotkaMockClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<SotkaHierarchy> getOptimeHierarchy(String optimeId) {
        Resource hierarchy;
        if (TEST_REALISATION_NOT_FOUND_SISU_CUR_ID.equals(optimeId)) {
            return Optional.empty();
        } else if (TEST_REALISATION_SISU_CUR_ID.equals(optimeId)) {
            hierarchy = hierarchy1;
        } else {
            hierarchy = hierarchy2;
        }

        SotkaHierarchy response = getResponse(hierarchy, new TypeReference<SotkaHierarchy>() {
        });
        response.optimeId = optimeId;
        return Optional.of(response);
    }

    @Override
    public List<SotkaHierarchy> getOptimeHierarchy(List<String> optimeIds) {
        if (optimeIds.isEmpty()) {
            return Collections.emptyList();
        }

        return optimeIds.stream()
            .map(this::getOptimeHierarchy)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(toList());
    }

    private <T> T getResponse(Resource resource, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(resource.getInputStream(), typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
