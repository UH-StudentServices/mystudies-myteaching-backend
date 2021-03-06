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

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "office_hours")
public class OfficeHours extends AbstractAuditingEntity implements Ownership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "name")
    public String name;

    @Column(name = "description")
    public String description;

    @Column(name = "additional_info")
    public String additionalInfo;

    @Column(name = "location")
    public String location;

    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "expiration_date", nullable = false)
    public LocalDate expirationDate;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    public User user;

    @ManyToMany
    @JoinTable(
        name = "office_hours_degree_programme",
        joinColumns = @JoinColumn(name = "office_hours_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "degree_programme_id", referencedColumnName = "id")
    )
    public List<DegreeProgramme> degreeProgrammes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "office_hours_teaching_language",
        joinColumns = @JoinColumn(name = "office_hours_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "teaching_language_id", referencedColumnName = "id")
    )
    public List<PersistentTeachingLanguage> teachingLanguages = new ArrayList<>();

    @Override
    public Long getOwnerId() {
        return user.id;
    }
}
