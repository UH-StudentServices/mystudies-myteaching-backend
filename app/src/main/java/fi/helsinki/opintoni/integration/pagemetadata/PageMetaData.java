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

package fi.helsinki.opintoni.integration.pagemetadata;

public class PageMetaData {

    public final String url;
    public final String title;
    public final String description;
    public final String image;
    public final String siteName;
    public final String locale;
    public final String type;

    public PageMetaData(String url,
                        String title,
                        String description,
                        String image,
                        String siteName,
                        String locale,
                        String type) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.image = image;
        this.siteName = siteName;
        this.locale = locale;
        this.type = type;
    }
}
