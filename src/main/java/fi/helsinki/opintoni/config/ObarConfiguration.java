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

import fi.helsinki.opintoni.integration.obar.ObarJWTService;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
@ConditionalOnProperty(prefix = "obar", name = "baseUrl")
public class ObarConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ObarConfiguration.class);

    @Autowired
    private AppConfiguration appConfiguration;

    @Bean
    public ObarJWTService getObarJwtService() {
        return new ObarJWTService(
            getKey(),
            SignatureAlgorithm.RS256,
            appConfiguration.getInteger("obar.jwtTimeout"),
            appConfiguration.get("logoutUrl"));
    }

    private Key getKey() {
        String pemPrivateKey = appConfiguration.get("obar.privateKey")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(pemPrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

        PrivateKey privateKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            privateKey = kf.generatePrivate(keySpec);
        } catch (GeneralSecurityException e) {
            logger.error("Failed to generate private key for obar", e);
        }
        return privateKey;
    }
}
