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

package fi.helsinki.opintoni.dto;

public class LinkFavoriteDto extends FavoriteDto {

    public final String url;
    public final String providerName;
    public final String title;
    public final String thumbnailUrl;
    public final Integer thumbnailWidth;
    public final Integer thumbnailHeight;

    public LinkFavoriteDto(Long id,
                           String type,
                           String url,
                           String providerName,
                           String title,
                           String thumbnailUrl,
                           Integer thumbnailWidth,
                           Integer thumbnailHeight) {
        super(id, type);
        this.url = url;
        this.providerName = providerName;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
    }

}
