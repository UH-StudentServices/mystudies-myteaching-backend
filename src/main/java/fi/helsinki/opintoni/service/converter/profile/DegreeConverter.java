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

package fi.helsinki.opintoni.service.converter.profile;

import fi.helsinki.opintoni.domain.profile.ComponentVisibility;
import fi.helsinki.opintoni.domain.profile.Degree;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.dto.profile.DegreeDto;
import fi.helsinki.opintoni.web.rest.privateapi.profile.degree.UpdateDegree;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class DegreeConverter {

    public DegreeDto toDto(Degree degree) {
        DegreeDto degreeDto = new DegreeDto();
        degreeDto.title = degree.title;
        degreeDto.institution = degree.institution;
        degreeDto.description = degree.description;
        degreeDto.dateOfDegree = degree.dateOfDegree;
        degreeDto.visibility = degree.visibility.toString();
        return degreeDto;
    }

    public Degree toEntity(UpdateDegree updateDegree, Profile profile, int orderIndex) {
        Degree degree = new Degree();
        degree.title = updateDegree.title;
        degree.institution = updateDegree.institution;
        degree.description = updateDegree.description;
        degree.dateOfDegree = updateDegree.dateOfDegree;
        degree.profile = profile;
        degree.orderIndex = orderIndex;
        degree.visibility = StringUtils.isNotBlank(updateDegree.visibility) ?
            ComponentVisibility.Visibility.valueOf(updateDegree.visibility) :
            ComponentVisibility.Visibility.PUBLIC;

        return degree;
    }
}
