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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio.contactinformation;

import com.google.common.collect.Lists;
import fi.helsinki.opintoni.domain.portfolio.ContactInformation;
import org.hibernate.validator.constraints.Email;

import java.util.List;

public class UpdateContactInformation {

    @Email
    public String email;
    public String phoneNumber;
    public String workNumber;
    public String workMobile;
    public String title;
    public String faculty;
    public String financialUnit;
    public String workAddress;
    public String workPostcode;

    public List<UpdateSomeLink> someLinks = Lists.newArrayList();

    public UpdateContactInformation(ContactInformation contactInformation) {
        this.email = contactInformation.email;
        this.phoneNumber = contactInformation.phoneNumber;
        this.workNumber = contactInformation.workNumber;
        this.workMobile = contactInformation.workMobile;
        this.title = contactInformation.title;
        this.faculty = contactInformation.faculty;
        this.financialUnit = contactInformation.financialUnit;
        this.workAddress = contactInformation.workAddress;
        this.workPostcode = contactInformation.workPostcode;
    }

    public UpdateContactInformation() {
    }
}
