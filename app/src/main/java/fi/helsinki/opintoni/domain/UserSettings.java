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

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_settings")
public class UserSettings extends AbstractAuditingEntity implements Ownership {

    @Id
    @GeneratedValue
    public Long id;

    @Column(name = "background_filename")
    public String backgroundFilename;

    @Column(name = "uploaded_background_filename")
    public String uploadedBackgroundFilename;

    @NotNull
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    public User user;

    @Override
    public Long getOwnerId() {
        return user.id;
    }

    @Embedded
    public UserAvatar userAvatar;

    public boolean hasAvatarImage() {
        return userAvatar != null && userAvatar.imageData != null;
    }

    @Column(name = "show_banner")
    public boolean showBanner;

    @Column(name = "cookie_consent")
    public boolean cookieConsent;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("backgroundFilename", backgroundFilename)
            .append("uploadedBackgroundFilename", uploadedBackgroundFilename)
            .toString();
    }
}
