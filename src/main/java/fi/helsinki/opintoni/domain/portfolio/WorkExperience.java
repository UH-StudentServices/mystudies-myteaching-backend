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
@Table(name = "work_experience")
public class WorkExperience extends AbstractAuditingEntity implements Ownership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotEmpty
    @Size(max = 255)
    public String employer;

    @Size(max = 255)
    @Column(name = "employer_url")
    public String employerUrl;

    @NotEmpty
    @Size(max = 255)
    @Column(name = "job_title", nullable = false)
    public String jobTitle;

    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "start_date", nullable = false)
    public LocalDate startDate;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "end_date")
    public LocalDate endDate;

    public String text;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    public Portfolio portfolio;

    @Column(name = "order_index")
    public int orderIndex;

    @NotNull
    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    public ComponentVisibility.Visibility visibility;

    @Override
    public Long getOwnerId() {
        return portfolio.getOwnerId();
    }
}
