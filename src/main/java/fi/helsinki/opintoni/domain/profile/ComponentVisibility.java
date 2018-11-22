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
@Table(name = "component_visibility")
public class ComponentVisibility extends AbstractAuditingEntity implements Ownership {

    public enum Visibility {
        PRIVATE, PUBLIC;

        public boolean isPublic() {
            return this == PUBLIC;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "profile_id")
    public Profile profile;

    @Column(name = "component")
    @Enumerated(EnumType.STRING)
    public ProfileComponent component;

    @Column(name = "teacher_profile_section")
    @Enumerated(EnumType.STRING)
    public TeacherProfileSection teacherProfileSection;

    @Column(name = "instance_name")
    public String instanceName;

    @NotNull
    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    public Visibility visibility;

    @Override
    public Long getOwnerId() {
        return profile.getOwnerId();
    }
}
