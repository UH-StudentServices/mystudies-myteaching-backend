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

package fi.helsinki.opintoni.integration.pagemetadata.opengraph;

import fi.helsinki.opintoni.integration.pagemetadata.PageMetaData;
import fi.helsinki.opintoni.sampledata.OpenGraphSampleData;
import fi.helsinki.opintoni.sampledata.SampleDataFiles;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenGraphMetaDataParserTest {

    private OpenGraphPageMetaDataParser parser;

    @Before
    public void setup() {
        parser = new OpenGraphPageMetaDataParser();
    }

    @Test
    public void shouldParseMetaDataFromHead() {
        PageMetaData metaData = parser.parsePageMetaData(SampleDataFiles.toText("pagemetadata/document.html"));
        assertThat(metaData.description).isEqualTo(OpenGraphSampleData.DESCRIPTION);
        assertThat(metaData.image).isEqualTo(OpenGraphSampleData.IMAGE);
        assertThat(metaData.siteName).isEqualTo(OpenGraphSampleData.SITE_NAME);
        assertThat(metaData.title).isEqualTo(OpenGraphSampleData.TITLE);
        assertThat(metaData.type).isEqualTo(OpenGraphSampleData.TYPE);
        assertThat(metaData.url).isEqualTo(OpenGraphSampleData.URL);
    }

}
