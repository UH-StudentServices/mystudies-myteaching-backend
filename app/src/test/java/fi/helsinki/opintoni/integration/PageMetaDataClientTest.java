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

package fi.helsinki.opintoni.integration;


import fi.helsinki.opintoni.SpringTest;
import fi.helsinki.opintoni.exception.http.BadRequestException;
import fi.helsinki.opintoni.integration.pagemetadata.PageMetaData;
import fi.helsinki.opintoni.integration.pagemetadata.PageMetaDataClient;
import fi.helsinki.opintoni.sampledata.OpenGraphSampleData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class PageMetaDataClientTest extends SpringTest {

    @Autowired
    private PageMetaDataClient pageMetaDataClient;

    @Test
    public void shouldGetAndParsePageMetaData() {
        metaDataServer.expectMetaDataRequest();
        PageMetaData metaData = pageMetaDataClient.getPageMetaData(OpenGraphSampleData.URL);
        assertEquals(OpenGraphSampleData.TITLE, metaData.title);
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowBadRequestException() {
        metaDataServer.expectMetaDataNotFound();
        pageMetaDataClient.getPageMetaData(OpenGraphSampleData.INVALID_URL);
    }

}
