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

package fi.helsinki.opintoni.service.usefullink;

import fi.helsinki.opintoni.domain.UsefulLink;
import fi.helsinki.opintoni.domain.UsefulLink.UsefulLinkType;
import fi.helsinki.opintoni.dto.OrderUsefulLinksDto;
import fi.helsinki.opintoni.dto.UsefulLinkDto;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.repository.UsefulLinkRepository;
import fi.helsinki.opintoni.repository.UserRepository;
import fi.helsinki.opintoni.service.converter.UsefulLinkConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class UsefulLinkTransactionalService {

    private final UsefulLinkRepository usefulLinkRepository;
    private final UserRepository userRepository;
    private final UsefulLinkConverter usefulLinkConverter;

    @Autowired
    public UsefulLinkTransactionalService(UsefulLinkRepository usefulLinkRepository,
                                          UserRepository userRepository,
                                          UsefulLinkConverter usefulLinkConverter) {
        this.usefulLinkRepository = usefulLinkRepository;
        this.userRepository = userRepository;
        this.usefulLinkConverter = usefulLinkConverter;
    }

    public UsefulLinkDto insert(final Long userId, final UsefulLinkDto usefulLinkDto, Locale locale) {
        UsefulLink usefulLink = new UsefulLink();
        usefulLink.type = UsefulLinkType.USER_DEFINED;
        usefulLink.url = usefulLinkDto.url;
        usefulLink.description = usefulLinkDto.description;
        usefulLink.orderIndex = usefulLinkRepository.getMaxOrderIndex(userId) + 1;
        usefulLink.user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(""));
        return usefulLinkConverter.toDto(usefulLinkRepository.save(usefulLink), locale);
    }

    public void delete(final Long usefulLinkId) {
        usefulLinkRepository.deleteById(usefulLinkId);
    }

    public List<UsefulLinkDto> findByUserId(Long userId, Locale locale) {
        return usefulLinkRepository.findByUserIdOrderByOrderIndexAsc(userId)
            .stream()
            .map(usefulLink -> usefulLinkConverter.toDto(usefulLink, locale))
            .collect(Collectors.toList());
    }

    public UsefulLinkDto update(Long usefulLinkId, UsefulLinkDto usefulLinkDto, Locale locale) {
        UsefulLink usefulLink = usefulLinkRepository.findById(usefulLinkId).orElseThrow(() -> new NotFoundException(""));
        usefulLink.url = usefulLinkDto.url;
        usefulLink.description = usefulLinkDto.description;
        return usefulLinkConverter.toDto(usefulLinkRepository.save(usefulLink), locale);
    }

    public List<UsefulLinkDto> updateOrder(Long userId, OrderUsefulLinksDto orderUsefulLinksDto, Locale locale) {
        List<UsefulLink> usefulLinks = usefulLinkRepository.findByUserIdOrderByOrderIndexAsc(userId);

        Map<Long, UsefulLink> usefulLinkMap = usefulLinks.stream()
            .collect(Collectors.toMap(u -> u.id, u -> u));

        IntStream
            .range(0, orderUsefulLinksDto.usefulLinkIds.size())
            .forEach(i -> {
                Long usefulLinkId = orderUsefulLinksDto.usefulLinkIds.get(i);
                usefulLinkMap.get(usefulLinkId).orderIndex = i;
            });

        return usefulLinks.stream()
            .sorted(UsefulLink::compareTo)
            .map(usefulLink -> usefulLinkConverter.toDto(usefulLink, locale))
            .collect(Collectors.toList());
    }

    public void save(List<UsefulLink> usefulLinks) {
        usefulLinkRepository.saveAll(usefulLinks);
    }
}
