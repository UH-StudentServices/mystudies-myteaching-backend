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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.helsinki.opintoni.domain.*;
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.dto.FavoriteDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.FavoriteRepository;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.FavoriteConverter;
import fi.helsinki.opintoni.web.rest.privateapi.InsertLinkFavoriteRequest;
import fi.helsinki.opintoni.web.rest.privateapi.InsertTwitterFavoriteRequest;
import fi.helsinki.opintoni.web.rest.privateapi.SaveRssFavoriteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteConverter favoriteConverter;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final FavoriteProperties favoriteProperties;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository,
                           FavoriteConverter favoriteConverter,
                           UserRepository userRepository,
                           FavoriteProperties favoriteProperties,
                           PortfolioRepository portfolioRepository) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteConverter = favoriteConverter;
        this.userRepository = userRepository;
        this.favoriteProperties = favoriteProperties;
        this.portfolioRepository = portfolioRepository;
    }

    public List<FavoriteDto> findByPortfolioId(final Long portfolioId) {
        return favoriteRepository.findByPortfolioIdOrderByOrderIndexAsc(portfolioId)
            .stream()
            .map(favoriteConverter::toDto)
            .collect(Collectors.toList());
    }

    public List<FavoriteDto> findByUserId(final Long userId) {
        return favoriteRepository.findByUserIdOrderByOrderIndexAsc(userId)
            .stream()
            .filter(f -> f.isPortfolio() == false)
            .map(favoriteConverter::toDto)
            .collect(Collectors.toList());
    }

    public FavoriteDto saveRssFavorite(Long userId, SaveRssFavoriteRequest saveRssFavoriteRequest) {
        int maxOrderIndex = orderIndex().apply(userId);

        RssFavorite favorite = new RssFavorite();
        favorite.url = saveRssFavoriteRequest.url;
        favorite.type = Favorite.Type.RSS;
        favorite.orderIndex = maxOrderIndex + 1;
        favorite.user = userRepository.findOne(userId);

        favoriteRepository.save(favorite);

        return favoriteConverter.toDto(favorite);
    }

    public FavoriteDto insertUnisportFavorite(Long userId) {
        UnisportFavorite favorite = new UnisportFavorite();
        favorite.type = Favorite.Type.UNISPORT;
        favorite.orderIndex = orderIndex().apply(userId) + 1;
        favorite.user = userRepository.findOne(userId);

        favoriteRepository.save(favorite);

        return favoriteConverter.toDto(favorite);
    }

    private static final ImmutableList<Favorite.Type> FLAMMA_TYPES = ImmutableList.of(
        Favorite.Type.FLAMMA_NEWS, Favorite.Type.FLAMMA_EVENTS);

    public FavoriteDto insertFlammaFavorite(Long userId, String typeString) {
        Favorite.Type type = Favorite.Type.valueOf(typeString);
        if (!FLAMMA_TYPES.contains(type)) {
            throw new IllegalArgumentException(
                String.format("Illegal Flamma type: %s", typeString));
        }
        Favorite favorite = new Favorite();
        favorite.type = type;
        favorite.orderIndex = orderIndex().apply(userId) + 1;
        favorite.user = userRepository.findOne(userId);

        favoriteRepository.save(favorite);
        return favoriteConverter.toDto(favorite);
    }

    public FavoriteDto insertLinkFavoriteForPortfolio(final Long userId,
                                                      final InsertLinkFavoriteRequest insertRequest,
                                                      final Long portfolioId) {
        LinkFavorite.Builder builder = new LinkFavorite.Builder();

        LinkFavorite favorite = builder
            .user(userRepository.findOne(userId))
            .orderIndex(portfolioOrderIndex().apply(userId, portfolioId) + 1)
            .providerName(insertRequest.providerName)
            .url(insertRequest.url)
            .title(insertRequest.title)
            .type(Favorite.Type.LINK)
            .thumbnailUrl(insertRequest.thumbnailUrl)
            .thumbnailHeight(insertRequest.thumbnailHeight)
            .thumbnailWidth(insertRequest.thumbnailWidth)
            .portfolio(portfolioRepository.findOne(portfolioId))
            .build();

        favoriteRepository.save(favorite);
        return favoriteConverter.toDto(favorite);
    }

    public FavoriteDto insertUnicafeFavorite(Long userId, Integer restaurantId) {
        UnicafeFavorite favorite = new UnicafeFavorite();
        favorite.type = Favorite.Type.UNICAFE;
        favorite.user = userRepository.findOne(userId);
        favorite.orderIndex = orderIndex().apply(userId) + 1;
        favorite.restaurantId = restaurantId;

        favoriteRepository.save(favorite);
        return favoriteConverter.toDto(favorite);
    }

    public FavoriteDto updateUnicafeFavorite(Integer restaurantId, Long unicafeFavoriteId) {
        UnicafeFavorite unicafeFavorite = (UnicafeFavorite) favoriteRepository.findOne(unicafeFavoriteId);
        unicafeFavorite.restaurantId = restaurantId;
        return favoriteConverter.toDto(favoriteRepository.save(unicafeFavorite));
    }

    public FavoriteDto insertTwitterFavorite(Long userId, InsertTwitterFavoriteRequest request) {
        return insertTwitterFavorite(userId, request, null);
    }

    private FavoriteDto insertTwitterFavorite(Long userId, InsertTwitterFavoriteRequest request, Portfolio portfolio) {
        TwitterFavorite favorite = new TwitterFavorite();
        favorite.type = Favorite.Type.TWITTER;
        favorite.user = userRepository.findOne(userId);
        favorite.orderIndex = orderIndex().apply(userId) + 1;
        favorite.portfolio = portfolio;

        favorite.feedType = TwitterFavorite.FeedType.valueOf(request.feedType);
        favorite.value = request.value;

        favoriteRepository.save(favorite);
        return favoriteConverter.toDto(favorite);
    }

    public FavoriteDto insertTwitterFavoriteForPortfolio(Long userId, InsertTwitterFavoriteRequest request, Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow(() -> new NotFoundException("Portfolio not found"));
        return insertTwitterFavorite(userId, request, portfolio);
    }

    public void orderPortfolioFavorites(final Long userId, final List<Long> orderedFavoriteIds) {
        orderFavorites(userId, orderedFavoriteIds, true);
    }

    public void orderFavorites(final Long userId, final List<Long> orderedFavoriteIds) {
        orderFavorites(userId, orderedFavoriteIds, false);
    }

    private void orderFavorites(final Long userId, final List<Long> orderedFavoriteIds, final boolean portfolio) {
        favoriteRepository
            .findByUserIdOrderByOrderIndexAsc(userId).stream()
            .filter(f -> f.isPortfolio() == portfolio)
            .filter(f -> orderedFavoriteIds.contains(f.id))
            .forEach(f -> f.orderIndex = orderedFavoriteIds.indexOf(f.id));
    }

    public void deleteFavorite(final Long favoriteId) {
        favoriteRepository.delete(favoriteId);
    }

    private BiFunction<Long, Long, Integer> portfolioOrderIndex() {
        return favoriteRepository::getMaxOrderIndexInPortfolio;
    }

    private Function<Long, Integer> orderIndex() {
        return favoriteRepository::getMaxOrderIndex;
    }

    private void createFavorite(Map<String, String> favorite, List<Favorite> favorites) {
        switch (Favorite.Type.valueOf(favorite.get("type"))) {
            case UNICAFE:
                favorites.add(createUnicafeFavorite(Integer.parseInt(favorite.get("restaurantId"))));
                break;
            case RSS:
                favorites.add(createRssFavorite(favorite.get("url")));
                break;
            case TWITTER:
                favorites.add(createTwitterFavorite(favorite.get("value")));
                break;
            case FLAMMA_EVENTS:
            case FLAMMA_NEWS:
                favorites.add(createFlammaFavorite(Favorite.Type.valueOf(favorite.get("type"))));
                break;
            default:
                throw new IllegalArgumentException("unexpected favorite type: " + favorite.get("type"));
        }
    }

    public void createDefaultFavorites(final User user) {
        List<Favorite> favorites = Lists.newArrayList();

        List<Map<String, String>> defaultFavorites = favoriteProperties.getDefaultFavorites();

        defaultFavorites.forEach(f -> createFavorite(f, favorites));

        IntStream.range(0, favorites.size())
            .forEach(index -> {
                Favorite favorite = favorites.get(index);
                favorite.user = user;
                favorite.orderIndex = index;
                favoriteRepository.save(favorite);
            });
    }

    private Favorite createUnicafeFavorite(int restaurantId) {
        UnicafeFavorite unicafeFavorite = new UnicafeFavorite();
        unicafeFavorite.type = Favorite.Type.UNICAFE;
        unicafeFavorite.restaurantId = restaurantId;
        return unicafeFavorite;
    }

    private Favorite createRssFavorite(String url) {
        RssFavorite rssFavorite = new RssFavorite();
        rssFavorite.type = Favorite.Type.RSS;
        rssFavorite.url = url;
        return rssFavorite;
    }

    private Favorite createTwitterFavorite(String value) {
        TwitterFavorite twitterFavorite = new TwitterFavorite();
        twitterFavorite.type = Favorite.Type.TWITTER;
        twitterFavorite.feedType = TwitterFavorite.FeedType.USER_TIMELINE;
        twitterFavorite.value = value;
        return twitterFavorite;
    }

    private Favorite createFlammaFavorite(Favorite.Type type) {
        Favorite flammaFavorite = new Favorite();
        flammaFavorite.type = type;
        return flammaFavorite;
    }
}
