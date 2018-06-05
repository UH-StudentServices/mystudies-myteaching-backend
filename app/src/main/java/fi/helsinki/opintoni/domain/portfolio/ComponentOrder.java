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

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "component_order")
public class ComponentOrder extends AbstractAuditingEntity implements Ownership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "portfolio_id")
    public Portfolio portfolio;

    @Column(name = "component")
    @Enumerated(EnumType.STRING)
    public PortfolioComponent component;

    @Column(name = "instance_name")
    public String instanceName;

    @NotNull
    @Column(name = "order_value")
    public Integer orderValue;

    @Override
    public Long getOwnerId() {
        return portfolio.getOwnerId();
    }
}
