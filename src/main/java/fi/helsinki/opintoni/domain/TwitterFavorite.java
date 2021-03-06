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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "twitter_favorite")
@PrimaryKeyJoinColumn(name = "id")
public class TwitterFavorite extends Favorite {

    public enum FeedType {
        USER_TIMELINE
    }

    @NotNull
    @Column(name = "feed_type")
    @Enumerated(EnumType.STRING)
    public FeedType feedType;

    @NotNull
    @Column(name = "value")
    public String value;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", super.id)
            .append("type", super.type)
            .append("orderIndex", super.orderIndex)
            .append("feedType", feedType)
            .append("value", value)
            .toString();
    }
}
