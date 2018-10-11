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

package fi.helsinki.opintoni.service.portfolio;

import fi.helsinki.opintoni.dto.portfolio.SomeLinkDto;
import fi.helsinki.opintoni.repository.portfolio.SomeLinkRepository;
import fi.helsinki.opintoni.service.DtoService;
import fi.helsinki.opintoni.service.converter.SomeLinkConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SomeLinkService extends DtoService {

    private final SomeLinkRepository someLinkRepository;
    private final SomeLinkConverter someLinkConverter;

    @Autowired
    public SomeLinkService(SomeLinkRepository someLinkRepository, SomeLinkConverter someLinkConverter) {
        this.someLinkRepository = someLinkRepository;
        this.someLinkConverter = someLinkConverter;
    }

    public List<SomeLinkDto> findByPortfolioId(Long portfolioId) {
        return getDtos(portfolioId,
            someLinkRepository::findByPortfolioId,
            someLinkConverter::toDto);
    }
}
