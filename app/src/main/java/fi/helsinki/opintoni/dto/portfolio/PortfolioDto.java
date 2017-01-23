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

package fi.helsinki.opintoni.dto.portfolio;

import fi.helsinki.opintoni.domain.portfolio.PortfolioVisibility;
import fi.helsinki.opintoni.dto.FavoriteDto;

import java.util.List;

public class PortfolioDto {
    public Long id;
    public String lang;
    public String url;
    public String intro;
    public String ownerName;
    public String backgroundUri;
    public PortfolioVisibility visibility;
    public String avatarUrl;
    public List<ComponentVisibilityDto> componentVisibilities;
    public List<LanguageProficiencyDto> languageProficiencies;
    public List<FreeTextContentDto> freeTextContent;
    public List<FavoriteDto> favorites;
    public List<WorkExperienceDto> workExperience;
    public List<SampleDto> samples;
    public JobSearchDto jobSearch;
    public ContactInformationDto contactInformation;
    public List<DegreeDto> degrees;
    public List<KeywordDto> keywords;
    public SummaryDto summary;
}
