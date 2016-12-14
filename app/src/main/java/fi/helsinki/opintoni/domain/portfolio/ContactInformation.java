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

package fi.helsinki.opintoni.domain.portfolio;

import fi.helsinki.opintoni.domain.AbstractAuditingEntity;
import fi.helsinki.opintoni.domain.Ownership;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "contact_information")
public class ContactInformation extends AbstractAuditingEntity implements Ownership {

    @Id
    @GeneratedValue
    public Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "portfolio_id")
    public Portfolio portfolio;

    @Column(name = "phone_number")
    public String phoneNumber;

    @Email
    @Column(name = "email")
    public String email;

    @Override
    public Long getOwnerId() {
        return portfolio.getOwnerId();
    }
}
