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

package fi.helsinki.opintoni.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_account")
public class User extends AbstractAuditingEntity {

    @Id
    @GeneratedValue
    public Long id;

    /*
     * eduPersonPrincipalName is the unique identifier in SSO (it will never change)
     */
    @NotNull
    @Column(name = "edu_person_principal_name")
    public String eduPersonPrincipalName;

    @NotNull
    @Column(name = "oodi_person_id")
    public String oodiPersonId;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("eduPersonPrincipalName", eduPersonPrincipalName)
            .toString();
    }

}
