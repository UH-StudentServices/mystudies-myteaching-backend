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

import fi.helsinki.opintoni.domain.profile.ComponentOrder;
import fi.helsinki.opintoni.domain.profile.Profile;
import fi.helsinki.opintoni.dto.profile.ComponentOrderDto;
import org.springframework.stereotype.Component;

@Component
public class ComponentOrderConverter {

    public ComponentOrderDto toDto(ComponentOrder componentOrder) {
        ComponentOrderDto componentOrderDto = new ComponentOrderDto();
        componentOrderDto.component = componentOrder.component;
        componentOrderDto.orderValue = componentOrder.orderValue;

        if (componentOrder.instanceName != null) {
            componentOrderDto.instanceName = componentOrder.instanceName;
        }

        return componentOrderDto;
    }

    public ComponentOrder toEntity(Profile profile, ComponentOrderDto componentOrderDto) {
        ComponentOrder componentOrder = new ComponentOrder();

        componentOrder.profile = profile;
        componentOrder.component = componentOrderDto.component;
        componentOrder.orderValue = componentOrderDto.orderValue;

        if (componentOrderDto.instanceName != null) {
            componentOrder.instanceName = componentOrderDto.instanceName;
        }

        return componentOrder;
    }
}
