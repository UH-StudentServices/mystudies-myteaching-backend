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

package fi.helsinki.opintoni.integration.esb;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ESBEmployeeInfoRecord {
    public String recordType;
    public String workNumber;
    public String workMobile;
    public String workAddress;
    public String workPostcode;
    public String title;
    public String email;
    public List<ESBEmployeeInfoOrganization> hrOrganisations = newArrayList();
    public List<ESBEmployeeInfoOrganization> ocOrganisations = newArrayList();
}
