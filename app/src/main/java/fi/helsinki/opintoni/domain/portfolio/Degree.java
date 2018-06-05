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
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "degree")
public class Degree extends AbstractAuditingEntity implements Ownership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "date_of_degree", nullable = false)
    public LocalDate dateOfDegree;

    @NotEmpty
    @Size(max = 255)
    public String title;

    @Size(max = 500)
    public String description;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    public Portfolio portfolio;

    @Override
    public Long getOwnerId() {
        return portfolio.getOwnerId();
    }
}
