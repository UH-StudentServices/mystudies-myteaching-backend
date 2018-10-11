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

package fi.helsinki.opintoni.service.converter.portfolio;

import fi.helsinki.opintoni.domain.portfolio.FreeTextContent;
import fi.helsinki.opintoni.dto.portfolio.FreeTextContentDto;
import org.springframework.stereotype.Component;

@Component
public class FreeTextContentConverter {
    public FreeTextContentDto toDto(FreeTextContent freeTextContent) {
        FreeTextContentDto freeTextContentDto = new FreeTextContentDto();
        freeTextContentDto.id = freeTextContent.id;
        freeTextContentDto.title = freeTextContent.title;
        freeTextContentDto.text = freeTextContent.text;
        freeTextContentDto.instanceName = freeTextContent.instanceName;

        if (freeTextContent.teacherPortfolioSection != null) {
            freeTextContentDto.portfolioSection = freeTextContent.teacherPortfolioSection.toString();
        }

        return freeTextContentDto;
    }
}
