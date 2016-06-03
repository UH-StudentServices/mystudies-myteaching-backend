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
