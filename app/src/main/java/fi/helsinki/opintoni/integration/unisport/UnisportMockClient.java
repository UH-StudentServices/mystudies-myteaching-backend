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

package fi.helsinki.opintoni.integration.unisport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

public class UnisportMockClient implements UnisportClient {

    @Value("classpath:sampledata/unisport/user.json")
    private Resource userResource;

    @Value("classpath:sampledata/unisport/user-reservations.json")
    private Resource userReservationsResource;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Optional<UnisportUser> getUnisportUserByPrincipal(String username) {
        return Optional.ofNullable(getResponse(userResource, new TypeReference<UnisportUser>() {
        }));
    }

    @Override
    public UnisportUserReservations getUserReservations(Long unisportUserId, Locale locale) {
        return getResponse(userResource, new TypeReference<UnisportUserReservations>() {
        });
    }

    public <T> T getResponse(Resource resource, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(resource.getInputStream(), typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
