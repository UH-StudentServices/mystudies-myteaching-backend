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

package fi.helsinki.opintoni.config.http;

import fi.helsinki.opintoni.config.AppConfiguration;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;

public class SSLRequestFactory {

    private SSLRequestFactory() {
    }

    public static ClientHttpRequestFactory clientHttpRequestFactory(AppConfiguration appConfiguration,
                                                                    SSLContext sslContext) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        factory.setReadTimeout(appConfiguration.getInteger("httpClient.readTimeout"));
        factory.setConnectTimeout(appConfiguration.getInteger("httpClient.connectTimeout"));

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = poolingHttpClientConnectionManager(sslContext);

        poolingHttpClientConnectionManager.setMaxTotal(appConfiguration.getInteger("httpClient.maxTotal"));
        poolingHttpClientConnectionManager
            .setDefaultMaxPerRoute(appConfiguration.getInteger("httpClient.defaultMaxPerRoute"));

        CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setConnectionManager(poolingHttpClientConnectionManager)
            .build();

        factory.setHttpClient(httpClient);

        return new BufferingClientHttpRequestFactory(factory);
    }

    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(SSLContext sslContext) {
        if (sslContext != null) {
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
            Registry socketFactoryRegistry = RegistryBuilder.create().register("https", sslConnectionSocketFactory).build();

            return new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } else {
            return new PoolingHttpClientConnectionManager();
        }
    }
}
