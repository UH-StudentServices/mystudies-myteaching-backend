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

import fi.helsinki.opintoni.integration.optime.OptimeClient;
import fi.helsinki.opintoni.integration.optime.OptimeHttpClient;
import fi.helsinki.opintoni.integration.optime.OptimeMockClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OptimeConfiguration {
    @Value("${optime.client.implementation:mock}")
    private String clientImplementation;

    @Bean
    OptimeClient optimeClient() {
        return "mock".equals(clientImplementation) ?
            new OptimeMockClient() :
            new OptimeHttpClient();
    }

    @Value("${optime.useOptimeFeedForWebCalendar:false}")
    public boolean useOptimeFeedForWebCalendar;
}
