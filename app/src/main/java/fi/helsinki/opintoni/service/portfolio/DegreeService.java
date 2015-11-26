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

import fi.helsinki.opintoni.domain.portfolio.Degree;
import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.dto.portfolio.DegreeDto;
import fi.helsinki.opintoni.repository.portfolio.DegreeRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.DtoService;
import fi.helsinki.opintoni.service.converter.DegreeConverter;
import fi.helsinki.opintoni.web.rest.privateapi.portfolio.degree.UpdateDegree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DegreeService extends DtoService {

    private final DegreeRepository degreeRepository;
    private final DegreeConverter degreeConverter;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public DegreeService(DegreeRepository degreeRepository,
                         DegreeConverter degreeConverter,
                         PortfolioRepository portfolioRepository) {
        this.degreeRepository = degreeRepository;
        this.degreeConverter = degreeConverter;
        this.portfolioRepository = portfolioRepository;
    }

    public List<DegreeDto> findByPortfolioId(Long portfolioId) {
        return getDtos(portfolioId,
            degreeRepository::findByPortfolioId,
            degreeConverter::toDto);
    }

    public List<DegreeDto> updateDegrees(Long portfolioId, List<UpdateDegree> updateDegrees) {
        Portfolio portfolio = portfolioRepository.findOne(portfolioId);

        degreeRepository.delete(degreeRepository.findByPortfolioId(portfolio.id));

        updateDegrees.forEach(updateDegree -> {
            Degree degree = new Degree();
            degree.title = updateDegree.title;
            degree.description = updateDegree.description;
            degree.dateOfDegree = updateDegree.dateOfDegree;
            degree.portfolio = portfolio;
            degreeRepository.save(degree);
        });

        return getDtos(portfolioId,
            degreeRepository::findByPortfolioId,
            degreeConverter::toDto);
    }
}
