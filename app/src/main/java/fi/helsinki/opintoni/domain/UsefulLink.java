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

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "useful_link")
public class UsefulLink extends AbstractAuditingEntity implements Ownership, Comparable<UsefulLink> {

    public enum UsefulLinkType {
        DEFAULT, USER_DEFINED
    }

    @Id
    @GeneratedValue
    public Long id;

    @Size(max = 500)
    public String url;

    @NotBlank
    @Size(max = 100)
    public String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    public User user;

    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public UsefulLinkType type;

    @Column(name = "order_index")
    public int orderIndex;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "localized_text_id")
    public LocalizedText localizedUrl;

    public UsefulLink() {
    }

    public boolean hasLocalizedUrl() {
        return localizedUrl != null;
    }

    @Override
    public Long getOwnerId() {
        return user.id;
    }

    @Override
    public final int compareTo(UsefulLink otherUsefulLink) {
        return Integer.compare(this.orderIndex, otherUsefulLink.orderIndex);
    }
}
