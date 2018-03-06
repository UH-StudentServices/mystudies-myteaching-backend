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

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "favorite")
@Inheritance(strategy = InheritanceType.JOINED)
public class Favorite extends AbstractAuditingEntity implements Ownership {

    public enum Type {
        TWITTER,
        RSS,
        LINK,
        UNICAFE,
        UNISPORT
    }

    @Column(name = "portfolio")
    public boolean portfolio;

    @Id
    @GeneratedValue
    public Long id;

    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public Type type;

    @Column(name = "order_index")
    public int orderIndex;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @Override
    public Long getOwnerId() {
        return user.id;
    }

    public boolean isPortfolio() {
        return portfolio;
    }
}
