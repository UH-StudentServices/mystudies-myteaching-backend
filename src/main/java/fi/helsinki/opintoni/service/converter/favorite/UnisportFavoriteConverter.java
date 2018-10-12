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

package fi.helsinki.opintoni.service.converter.favorite;

import fi.helsinki.opintoni.dto.favorite.UnisportEventDto;
import fi.helsinki.opintoni.dto.favorite.UnisportReservationsDto;
import fi.helsinki.opintoni.integration.unisport.UnisportEvent;
import fi.helsinki.opintoni.integration.unisport.UnisportUserReservations;

import java.util.Collection;
import java.util.stream.Collectors;

public class UnisportFavoriteConverter {

    private final String authorizationUrl;

    public UnisportFavoriteConverter(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public UnisportReservationsDto toDto(UnisportUserReservations unisportUserReservations) {
        return new UnisportReservationsDto(unisportUserReservations.reservations.stream()
            .map(r -> r.events)
            .flatMap(Collection::stream)
            .map(this::toDto)
            .collect(Collectors.toList()));
    }

    private UnisportEventDto toDto(UnisportEvent unisportEvent) {
        return new UnisportEventDto(
            unisportEvent.url,
            unisportEvent.name,
            unisportEvent.venue,
            unisportEvent.startTime,
            unisportEvent.endTime);
    }

    public UnisportReservationsDto unauthorizedReservationsDto() {
        return new UnisportReservationsDto(authorizationUrl);
    }

}
