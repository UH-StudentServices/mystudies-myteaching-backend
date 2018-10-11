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

package fi.helsinki.opintoni.config;

import fi.helsinki.opintoni.integration.pagemetadata.PageMetaDataClient;
import fi.helsinki.opintoni.integration.pagemetadata.PageMetaDataHttpClient;
import fi.helsinki.opintoni.integration.pagemetadata.SpringPageMetaDataHttpClient;
import fi.helsinki.opintoni.integration.pagemetadata.opengraph.OpenGraphPageMetaDataParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Configuration
public class PageMetadataConfiguration {

    @Bean
    public RestTemplate metaDataRestTemplate() {
        RestTemplate template = new RestTemplate();
        template.getMessageConverters()
            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return template;
    }

    @Bean
    public PageMetaDataClient pageMetaDataClient() {
        return new PageMetaDataClient(new SpringPageMetaDataHttpClient(metaDataRestTemplate()), new OpenGraphPageMetaDataParser());
    }

    @Bean
    public PageMetaDataHttpClient pageMetaDataHttpClient() {
        return new SpringPageMetaDataHttpClient(metaDataRestTemplate());
    }

}
