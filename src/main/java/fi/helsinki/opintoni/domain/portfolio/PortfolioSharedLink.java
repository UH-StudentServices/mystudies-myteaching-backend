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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_shared_link")
public class PortfolioSharedLink extends AbstractAuditingEntity implements Ownership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    public Portfolio portfolio;

    @NotNull
    @Column(name = "shared_path_fragment", nullable = false, updatable = false, length = 36)
    public String sharedPathFragment;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "expiryDate")
    public LocalDateTime expiryDate;

    @Override
    public Long getOwnerId() {
        return portfolio.getOwnerId();
    }

    public boolean isActive() {
        return this.expiryDate == null || this.expiryDate.isAfter(LocalDateTime.now());
    }
}