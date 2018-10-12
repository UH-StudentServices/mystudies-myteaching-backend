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

import fi.helsinki.opintoni.exception.http.BadRequestException;

public class PageMetaDataClient {

    private final PageMetaDataHttpClient httpClient;

    private final PageMetaDataParser parser;

    public PageMetaDataClient(PageMetaDataHttpClient httpClient, PageMetaDataParser parser) {
        this.httpClient = httpClient;
        this.parser = parser;
    }

    public PageMetaData getPageMetaData(String pageUrl) {
        return httpClient
            .getPageBody(pageUrl)
            .map(parser::parsePageMetaData)
            .orElseThrow(() -> new BadRequestException("Page metadata not found for url " + pageUrl));
    }

}
