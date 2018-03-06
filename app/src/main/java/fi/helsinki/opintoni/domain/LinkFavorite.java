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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "link_favorite")
@PrimaryKeyJoinColumn(name = "id")
public class LinkFavorite extends Favorite {

    @NotNull
    @Column(name = "url")
    public String url;

    @Column(name = "provider_name")
    public String providerName;

    @Column(name = "title")
    public String title;

    @Column(name = "thumbnail_url")
    public String thumbnailUrl;

    @Column(name = "thumbnail_width")
    public Integer thumbnailWidth;

    @Column(name = "thumbnail_height")
    public Integer thumbnailHeight;

    // No-args constructor needed for Hibernate
    public LinkFavorite() {
    }

    private LinkFavorite(Builder builder) {
        super.id = builder.id;
        super.orderIndex = builder.orderIndex;
        super.type = builder.type;
        super.user = builder.user;
        super.portfolio = builder.portfolio;

        this.url = builder.url;
        this.providerName = builder.providerName;
        this.title = builder.title;
        this.thumbnailUrl = builder.thumbnailUrl;
        this.thumbnailWidth = builder.thumbnailWidth;
        this.thumbnailHeight = builder.thumbnailHeight;
    }

    public static class Builder {
        private Long id;
        private Type type;
        private int orderIndex;
        private User user;
        private String url;
        private String providerName;
        private String title;
        private String thumbnailUrl;
        private Integer thumbnailWidth;
        private Integer thumbnailHeight;
        private boolean portfolio;

        public LinkFavorite build() {
            return new LinkFavorite(this);
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder orderIndex(int orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder providerName(String providerName) {
            this.providerName = providerName;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder thumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public Builder thumbnailWidth(Integer thumbnailWidth) {
            this.thumbnailWidth = thumbnailWidth;
            return this;
        }

        public Builder thumbnailHeight(Integer thumbnailHeight) {
            this.thumbnailHeight = thumbnailHeight;
            return this;
        }

        public Builder portfolio(boolean portfolio) {
            this.portfolio = portfolio;
            return this;
        }
    }
}
