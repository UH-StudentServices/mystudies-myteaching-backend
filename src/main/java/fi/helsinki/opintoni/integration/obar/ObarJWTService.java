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

package fi.helsinki.opintoni.integration.obar;

import fi.helsinki.opintoni.security.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.helsinki.opintoni.integration.obar.Constants.DEFAULT_LANGUAGE;

public class ObarJWTService {

    private final Key obarSecretKey;
    private final SignatureAlgorithm signatureAlgorithm;
    private final Integer expirationTimeSeconds;
    private final String loginEndpoint;
    private final String logoutEndpoint;

    public ObarJWTService(Key obarSecretKey,
                          SignatureAlgorithm signatureAlgorithm,
                          Integer expirationTimeSeconds,
                          String loginEndpoint,
                          String logoutEndpoint) {
        this.obarSecretKey = obarSecretKey;
        this.signatureAlgorithm = signatureAlgorithm;
        this.expirationTimeSeconds = expirationTimeSeconds;
        this.loginEndpoint = loginEndpoint;
        this.logoutEndpoint = logoutEndpoint;
    }

    public String generateToken(final AppUser appUser, String language) {
        String currentLang = language != null ? language : DEFAULT_LANGUAGE.getCode();

        Map<String, Object> claims = new HashMap<>();
        claims.put("loginEndpoint", loginEndpoint);
        claims.put("logoutEndpoint", logoutEndpoint);
        claims.put("currentLang", currentLang);
        if (appUser != null) {
            List<String> nameParts = Arrays.asList(appUser.getCommonName().trim().split(" "));
            String lastName = nameParts.get(nameParts.size() - 1);
            String firstName = "";
            if (nameParts.size() > 1) {
                firstName = String.join(" ", nameParts.subList(0, nameParts.size() - 1));
            }
            claims.put("user", Map.of(
                "userName", appUser.getEduPersonPrincipalName().split("@")[0],
                "firstName", firstName,
                "lastName", lastName,
                "oodiId", appUser.getOodiPersonId()
            ));
        }
        return Jwts.builder().setClaims(claims).setExpiration(getExpirationDate()).signWith(signatureAlgorithm, obarSecretKey).compact();

    }

    private Date getExpirationDate() {
        return Date.from(LocalDateTime.now().plusSeconds(expirationTimeSeconds).atZone(ZoneId.systemDefault()).toInstant());
    }
}
