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

package fi.helsinki.opintoni.integration.unisport;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class ExpiringUnisportJWTService implements UnisportJWTService {

    private final Key unisportPrivateSecret;

    private final SignatureAlgorithm signatureAlgorithm;

    private final int tokenValidForSeconds;

    public ExpiringUnisportJWTService(Key unisportPrivateSecret, SignatureAlgorithm signatureAlgorithm, int tokenValidForSeconds) {
        this.unisportPrivateSecret = unisportPrivateSecret;
        this.signatureAlgorithm = signatureAlgorithm;
        this.tokenValidForSeconds = tokenValidForSeconds;
    }

    @Override
    public String generateToken(final Long unisportUserId) {
        return Jwts.builder()
            .setSubject(unisportUserId.toString())
            .setExpiration(getExpirationTime())
            .signWith(signatureAlgorithm, unisportPrivateSecret)
            .compact();
    }

    private Date getExpirationTime() {
        return Date.from(LocalDateTime.now().plusSeconds(tokenValidForSeconds).toInstant(ZoneOffset.UTC));
    }

}
