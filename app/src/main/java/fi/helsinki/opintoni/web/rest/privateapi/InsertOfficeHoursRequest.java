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

package fi.helsinki.opintoni.web.rest.privateapi;

import fi.helsinki.opintoni.dto.OfficeHoursDto;

import javax.validation.Valid;
import java.util.List;

public class InsertOfficeHoursRequest {

    @Valid
    public List<OfficeHoursDto> officeHours;

    public InsertOfficeHoursRequest() {
    }

    public InsertOfficeHoursRequest(List<OfficeHoursDto> officeHours) {
        this.officeHours = officeHours;
    }

    // JSR-303 validation requires getter and setter
    public List<OfficeHoursDto> getOfficeHours() {
        return officeHours;
    }

    // JSR-303 validation requires getter and setter
    public void setOfficeHours(List<OfficeHoursDto> officeHours) {
        this.officeHours = officeHours;
    }

}
