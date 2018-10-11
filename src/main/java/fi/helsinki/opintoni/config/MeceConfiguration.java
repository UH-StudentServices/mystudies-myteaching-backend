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

import fi.helsinki.opintoni.integration.mece.JWTService;
import fi.helsinki.opintoni.integration.mece.MeceJWTService;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
public class MeceConfiguration {

    @Autowired
    private AppConfiguration appConfiguration;

    @Bean
    public JWTService getJWTService() {
        return new MeceJWTService(getKey(), SignatureAlgorithm.HS256, appConfiguration.getInteger("session.timeout"));
    }

    private Key getKey() {
        return new SecretKeySpec(appConfiguration.get("meceSecretKey").getBytes(UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

}