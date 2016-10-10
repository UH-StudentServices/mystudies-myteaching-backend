package fi.helsinki.opintoni.config;

import fi.helsinki.opintoni.integration.mece.JWTService;
import fi.helsinki.opintoni.integration.mece.MeceJWTService;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import static java.nio.charset.StandardCharsets.*;

@Configuration
public class MeceConfiguration {

    @Autowired
    private AppConfiguration appConfiguration;

    @Bean
    public JWTService getJWTService() {
        return new MeceJWTService(getKey(), SignatureAlgorithm.HS256, appConfiguration.getInteger("server.session.timeout"));
    }

    private Key getKey() {
        return new SecretKeySpec(appConfiguration.get("meceSecretKey").getBytes(UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

}
