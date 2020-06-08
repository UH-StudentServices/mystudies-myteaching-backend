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

import java.io.IOException;

public class SotkaMockClient implements SotkaClient {

    private static final String TEST_REALISATION_ID = "234567891";
    private static final String TEST_REALISATION_NOT_FOUND_ID = "123456789";

    private final ObjectMapper objectMapper;

    @Value("classpath:sampledata/sotka/oodi_hierarchy.json")
    private Resource hierarchy1;

    @Value("classpath:sampledata/sotka/oodi_hierarchy_from_optime.json")
    private Resource hierarchy2;

    public SotkaMockClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public OodiHierarchy getOodiHierarchy(String oodiRealisationId) {
        if (TEST_REALISATION_NOT_FOUND_ID.equals(oodiRealisationId)) {
            return new OodiHierarchy();
        }

        Resource hierarchy = TEST_REALISATION_ID.equals(oodiRealisationId) ? hierarchy1 : hierarchy2;

        OodiHierarchy response = getResponse(hierarchy, new TypeReference<OodiHierarchy>() {
        });
        response.oodiId = oodiRealisationId;
        return response;
    }

    private <T> T getResponse(Resource resource, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(resource.getInputStream(), typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
