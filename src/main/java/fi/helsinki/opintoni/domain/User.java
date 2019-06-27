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
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_account")
public class User extends AbstractAuditingEntity {

    public enum AccountStatus {
        ACTIVE, INACTIVE;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /*
     * eduPersonPrincipalName is the unique identifier in SSO (it will never change)
     */
    @NotNull
    @Column(name = "edu_person_principal_name")
    public String eduPersonPrincipalName;

    @NotNull
    @Column(name = "person_id")
    public String personId;

    @NotNull
    @Column(name = "last_login_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime lastLoginDate;

    @Column(name = "account_active_until_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime accountActiveUntilDate;

    @NotNull
    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    public AccountStatus accountStatus;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("eduPersonPrincipalName", eduPersonPrincipalName)
            .toString();
    }
}
