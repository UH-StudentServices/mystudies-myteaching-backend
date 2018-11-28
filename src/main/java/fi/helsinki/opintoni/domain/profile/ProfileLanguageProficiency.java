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

package fi.helsinki.opintoni.domain.profile;

import fi.helsinki.opintoni.domain.AbstractAuditingEntity;
import fi.helsinki.opintoni.domain.Ownership;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "profile_language_proficiency")
public class ProfileLanguageProficiency extends AbstractAuditingEntity implements Ownership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Column(name = "language_name")
    public String languageName;

    @NotNull
    @Column(name = "proficiency")
    public String proficiency;

    @Column(name = "description")
    public String description;

    @NotNull
    @Column(name = "order_index", nullable = false)
    public Integer orderIndex;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "profile_id")
    public Profile profile;

    @NotNull
    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    public ComponentVisibility.Visibility visibility;

    @Override
    public Long getOwnerId() {
        return profile.getOwnerId();
    }
}
