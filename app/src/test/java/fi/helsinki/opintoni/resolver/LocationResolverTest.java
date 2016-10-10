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

package fi.helsinki.opintoni.resolver;

import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.dto.BuildingDto;
import fi.helsinki.opintoni.integration.oodi.OodiEvent;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationResolverTest extends SpringTest {

    @Autowired
    private LocationResolver locationResolver;

    @Test
    public void thatNullIsReturnedWithNullBuildingId() {
        BuildingDto buildingDto = locationResolver.getBuilding(new OodiEvent());
        assertThat(buildingDto).isNull();
    }

    @Test
    public void thatNullIsReturnedWhenAddressIsBlank() {
        OodiEvent oodiEvent = oodiEventWithBuilding(null, "12345");

        BuildingDto buildingDto = locationResolver.getBuilding(oodiEvent);
        assertThat(buildingDto).isNull();
    }

    @Test
    public void thatBuildingDtoIsReturnedWhenZipCodeIsBlankButAddressIsNot() {
        String street = "Osoite";
        OodiEvent oodiEvent = oodiEventWithBuilding(street, null);

        BuildingDto buildingDto = locationResolver.getBuilding(oodiEvent);
        assertThat(buildingDto).isNotNull();
        assertThat(buildingDto.street).isEqualTo(street);
    }

    @Test
    public void thatNullIsReturnedWhenZipCodeAndAddressAreBlank() {
        OodiEvent oodiEvent = oodiEventWithBuilding(null, null);

        BuildingDto buildingDto = locationResolver.getBuilding(oodiEvent);
        assertThat(buildingDto).isNull();
    }

    @Test
    public void thatBuildingUriIsReturned() {
        String street = "Osoite";
        String zip = "1345";

        OodiEvent oodiEvent = oodiEventWithBuilding(street, zip);

        BuildingDto buildingDto = locationResolver.getBuilding(oodiEvent);
        assertThat(buildingDto.street).isEqualTo(street);
        assertThat(buildingDto.zipCode).isEqualTo(zip);
    }

    private OodiEvent oodiEventWithBuilding(String street, String zip) {
        OodiEvent oodiEvent = new OodiEvent();
        oodiEvent.buildingStreet = street;
        oodiEvent.buildingZipCode = zip;
        return oodiEvent;
    }
}
