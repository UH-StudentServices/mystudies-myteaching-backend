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

import fi.helsinki.opintoni.domain.portfolio.*;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import org.apache.commons.lang.StringUtils;

public class LanguageProficiencyConverter {

    public static LanguageProficiencyDto toDto(PortfolioLanguageProficiency portfolioLanguageProficiency) {
        LanguageProficiencyDto languageProficiencyDto = new LanguageProficiencyDto();
        languageProficiencyDto.id = portfolioLanguageProficiency.id;
        languageProficiencyDto.languageName = portfolioLanguageProficiency.languageName;
        languageProficiencyDto.proficiency = portfolioLanguageProficiency.proficiency;
        languageProficiencyDto.description = portfolioLanguageProficiency.description;
        languageProficiencyDto.visibility = portfolioLanguageProficiency.visibility.toString();
        return languageProficiencyDto;
    }

    public static PortfolioLanguageProficiency toEntity(LanguageProficiencyDto languageProficiencyDto, Portfolio portfolio) {
        PortfolioLanguageProficiency portfolioLanguageProficiency = new PortfolioLanguageProficiency();
        portfolioLanguageProficiency.languageName = languageProficiencyDto.languageName;
        portfolioLanguageProficiency.proficiency = languageProficiencyDto.proficiency;
        portfolioLanguageProficiency.description = languageProficiencyDto.description;
        portfolioLanguageProficiency.portfolio = portfolio;
        portfolioLanguageProficiency.visibility = StringUtils.isNotBlank(languageProficiencyDto.visibility) ?
            ComponentVisibility.Visibility.valueOf(languageProficiencyDto.visibility) :
            ComponentVisibility.Visibility.PUBLIC;
        return portfolioLanguageProficiency;
    }
}
