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
import fi.helsinki.opintoni.integration.pagemetadata.PageMetaDataParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OpenGraphPageMetaDataParser implements PageMetaDataParser {

    private static final String OG_TITLE = "og:title";

    private static final String OG_TYPE = "og:type";

    private static final String OG_DESCRIPTION = "og:description";

    private static final String OG_IMAGE = "og:image";

    private static final String OG_URL = "og:url";

    private static final String OG_SITE_NAME = "og:site_name";

    private static final String OG_LOCALE = "og:locale";

    private static final String PROPERTY = "property";

    private static final String CONTENT = "content";

    @Override
    public PageMetaData parsePageMetaData(String htmlDocument) {
        Element head = Jsoup.parse(htmlDocument).head();

        return new PageMetaData(
            getOgAttributeValue(head, OG_URL),
            getOgAttributeValue(head, OG_TITLE),
            getOgAttributeValue(head, OG_DESCRIPTION),
            getOgAttributeValue(head, OG_IMAGE),
            getOgAttributeValue(head, OG_SITE_NAME),
            getOgAttributeValue(head, OG_LOCALE),
            getOgAttributeValue(head, OG_TYPE)
        );
    }

    private String getOgAttributeValue(Element head, String key) {
        String value = null;
        Elements elementsByAttributeValue = head.getElementsByAttributeValue(PROPERTY, key);
        if (elementsByAttributeValue != null && elementsByAttributeValue.size() == 1) {
            value = elementsByAttributeValue.get(0).attr(CONTENT);
        }
        return value;
    }

}
