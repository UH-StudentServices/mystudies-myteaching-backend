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
package fi.helsinki.opintoni.integration.mece;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

public class MeceJWTServiceTest {

    private static final String MECE_PRIVATE_SECRET = "FOOBAR";
    private static final String PRINCIPAL = "testuser";

    private MeceJWTService meceJWTService;

    @Before
    public void setup() {
        meceJWTService = new MeceJWTService(getKey(), SignatureAlgorithm.HS256);
    }

    @Test
    public void shouldReturnValidTokenWithPrincipal() {
        String token = meceJWTService.generateToken(PRINCIPAL);
        assert Jwts.parser().setSigningKey(getKey()).parseClaimsJws(token).getBody().getSubject().equals(PRINCIPAL);
    }

    private Key getKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(MECE_PRIVATE_SECRET);
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}
