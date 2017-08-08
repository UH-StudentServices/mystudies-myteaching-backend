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

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.domain.portfolio.PortfolioComponent;
import fi.helsinki.opintoni.dto.portfolio.ComponentHeadingDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import fi.helsinki.opintoni.service.portfolio.ComponentHeadingService;

import java.util.List;

public class ComponentHeadingServiceTest extends SpringTest {

    private Long portfolioId = 3L;

    @Autowired
    private ComponentHeadingService componentHeadingService;


    @Test
    public void thatHeadingIsSaved() {

        List<ComponentHeadingDto> componentHeadingDtoList = componentHeadingService.findByPortfolioId(3L);
        assertThat(componentHeadingDtoList).hasSize(0);
        ComponentHeadingDto componentHeadingDto = new ComponentHeadingDto();
        componentHeadingDto.component = PortfolioComponent.STUDIES;
        componentHeadingDto.heading = "Omat opinnot";
        ComponentHeadingDto updateResult = componentHeadingService.upsert(portfolioId, componentHeadingDto);
        assertThat(updateResult.component).isEqualTo(componentHeadingDto.component);
        assertThat(updateResult.heading).isEqualTo(componentHeadingDto.heading);
        componentHeadingDtoList = componentHeadingService.findByPortfolioId(3L);
        assertThat(componentHeadingDtoList).hasSize(1);
    }

    @Test
    public void thatHeadingIsUpdated() {

        ComponentHeadingDto componentHeadingDto = new ComponentHeadingDto();
        componentHeadingDto.component = PortfolioComponent.STUDIES;
        componentHeadingDto.heading = "Omat opinnot";
        componentHeadingService.upsert(portfolioId, componentHeadingDto);
        componentHeadingDto.heading = "Toisten opinnot";
        componentHeadingService.upsert(portfolioId, componentHeadingDto);
        List<ComponentHeadingDto> componentHeadingDtoList = componentHeadingService.findByPortfolioId(3L);
        assertThat(componentHeadingDtoList.get(0).heading).isEqualTo(componentHeadingDto.heading);
    }
    @Test
    public void thatHeadingIsDeleted() {

        ComponentHeadingDto componentHeadingDto = new ComponentHeadingDto();
        componentHeadingDto.component = PortfolioComponent.STUDIES;
        componentHeadingDto.heading = "Omat opinnot";
        componentHeadingService.upsert(portfolioId, componentHeadingDto);
        componentHeadingService.delete(portfolioId, PortfolioComponent.STUDIES);
        List<ComponentHeadingDto> componentHeadingDtoList = componentHeadingService.findByPortfolioId(3L);
        assertThat(componentHeadingDtoList).hasSize(0);
    }
}

