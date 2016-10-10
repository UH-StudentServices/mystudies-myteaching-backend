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
import org.jsoup.Jsoup;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "todo_item")
public class TodoItem extends AbstractAuditingEntity implements Ownership {

    public enum Status {
        OPEN, DONE
    }

    @Id
    @GeneratedValue
    public Long id;

    @NotBlank
    @Size(max = 500)
    @Column(name = "content")
    public String content;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    public User user;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    public Status status = Status.OPEN;

    @Override
    public Long getOwnerId() {
        return user.id;
    }

    @PrePersist
    @PreUpdate
    private void contentToText() {
        if (content != null) {
            content = Jsoup.parse(content).text();
        }
    }
}
