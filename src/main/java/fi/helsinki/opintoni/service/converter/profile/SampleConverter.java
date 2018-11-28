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

import fi.helsinki.opintoni.domain.profile.*;
import fi.helsinki.opintoni.dto.profile.SampleDto;
import fi.helsinki.opintoni.web.rest.privateapi.profile.sample.UpdateSample;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class SampleConverter {

    public SampleDto toDto(Sample sample) {
        SampleDto sampleDto = new SampleDto();

        sampleDto.description = sample.description;
        sampleDto.title = sample.title;
        sampleDto.id = sample.id;
        sampleDto.visibility = sample.visibility.toString();

        return sampleDto;
    }

    public Sample toEntity(UpdateSample updateSample, Profile profile, Integer orderIndex) {
        Sample sample = new Sample();
        sample.title = updateSample.title;
        sample.description = updateSample.description;
        sample.orderIndex = orderIndex;
        sample.profile = profile;
        sample.visibility = StringUtils.isNotBlank(updateSample.visibility) ?
            ComponentVisibility.Visibility.valueOf(updateSample.visibility) :
            ComponentVisibility.Visibility.PUBLIC;
        return sample;
    }
}
