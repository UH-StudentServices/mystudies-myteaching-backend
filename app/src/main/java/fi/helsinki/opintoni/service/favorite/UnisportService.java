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

package fi.helsinki.opintoni.service.favorite;

import fi.helsinki.opintoni.dto.favorite.UnisportReservationsDto;
import fi.helsinki.opintoni.integration.unisport.UnisportClient;
import fi.helsinki.opintoni.service.converter.favorite.UnisportFavoriteConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UnisportService {

    private final UnisportFavoriteConverter unisportFavoriteConverter;
    private final UnisportClient unisportClient;

    @Autowired
    public UnisportService(UnisportFavoriteConverter unisportFavoriteConverter, UnisportClient unisportClient) {
        this.unisportFavoriteConverter = unisportFavoriteConverter;
        this.unisportClient = unisportClient;
    }

    public UnisportReservationsDto getReservations(final String username, final Locale locale) {
        return unisportClient.getUnisportUserByPrincipal(username)
            .map(unisportUser -> unisportClient.getUserReservations(unisportUser.user, locale))
            .map(unisportFavoriteConverter::toDto)
            .orElseGet(unisportFavoriteConverter::unauthorizedReservationsDto);
    }

}
