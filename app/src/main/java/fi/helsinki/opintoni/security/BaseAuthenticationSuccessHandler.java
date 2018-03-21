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

package fi.helsinki.opintoni.security;

import com.google.common.net.HttpHeaders;
import fi.helsinki.opintoni.config.Constants;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.integration.oodi.OodiIntegrationException;
import fi.helsinki.opintoni.service.UserService;
import fi.helsinki.opintoni.util.AuditLogger;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static fi.helsinki.opintoni.config.Constants.NG_TRANSLATE_LANG_KEY;
import static fi.helsinki.opintoni.config.Constants.OPINTONI_HAS_LOGGED_IN;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class BaseAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = getLogger(BaseAuthenticationSuccessHandler.class);

    private UserService userService;

    private Environment env;

    private AuditLogger auditLogger;

    @Autowired
    public void initialize(UserService userService, Environment env, AuditLogger auditLogger) {
        this.userService = userService;
        this.env = env;
        this.auditLogger = auditLogger;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        AppUser appUser = (AppUser) authentication.getPrincipal();

        logUserLogin(appUser, request);

        try {
            syncUserWithDatabase(appUser);

            if (isFirstLogin(request)) {
                addLanguageCookieForUserPreferredLanguageIfSupported(appUser, response);
            }

            if (!env.acceptsProfiles(Constants.SPRING_PROFILE_DEMO)) {
                addHasLoggedInCookie(response);
            }
            handleAuthSuccess(response);
        } catch (OodiIntegrationException e) {
            handleAuthFailure(response);
        }
    }

    protected abstract void handleAuthSuccess(HttpServletResponse response) throws IOException;

    protected abstract void handleAuthFailure(HttpServletResponse response) throws IOException;

    private void syncUserWithDatabase(AppUser appUser) {
        Optional<User> user = getUserFromDb(appUser);
        if (user.isPresent()) {
            updateExistingUser(appUser, user.get());
        } else {
            createNewUser(appUser);
        }
    }

    private void logUserLogin(AppUser appUser, HttpServletRequest request) {
        String ipAddress = request.getHeader(HttpHeaders.X_FORWARDED_FOR);

        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }

        auditLogger.log(
            String.format("User logged in from ipAddress %s with eduPersonPrincipalName %s",
                ipAddress,
                appUser.getEduPersonPrincipalName()));
    }

    private Optional<User> getUserFromDb(AppUser appUser) {
        return userService.findFirstByEduPersonPrincipalName(appUser.getEduPersonPrincipalName());
    }

    private void createNewUser(AppUser appUser) {
        userService.createNewUser(appUser);
    }

    private void updateExistingUser(AppUser appUser, User user) {
        if (user.oodiPersonId == null) {
            user.oodiPersonId = appUser.getOodiPersonId();
            userService.save(user);
        }
    }

    private void addLanguageCookieForUserPreferredLanguageIfSupported(AppUser appUser, HttpServletResponse response) {
        String language = getLanguageCookieValue(appUser.getPreferredLanguage());

        if (language != null) {
            Cookie cookie = new Cookie(NG_TRANSLATE_LANG_KEY, language);
            addCookie(response, cookie);
        }
    }

    private boolean isFirstLogin(HttpServletRequest request) {
        return WebUtils.getCookie(request, OPINTONI_HAS_LOGGED_IN) == null;
    }

    private String getLanguageCookieValue(String preferredLanguage) {
        if (preferredLanguage != null) {
            List<String> availableLanguages = env.getRequiredProperty("language.available", List.class);

            try {
                Locale locale = Locale.forLanguageTag(StringUtils.replace(preferredLanguage, "_", "-"));

                String language = locale.getLanguage();

                return availableLanguages.contains(language) ? language : null;

            } catch (IllegalArgumentException e) {
                log.error(String.format("Failed to parse preferredLanguage %s", preferredLanguage));
            }
        }
        return null;
    }

    // NOTE: This cookie is relied on by courses.helsinki.fi for automatic user login
    // and should thus not be removed without consulting the course page team first.
    private void addHasLoggedInCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(OPINTONI_HAS_LOGGED_IN, Boolean.TRUE.toString());
        cookie.setMaxAge(Integer.MAX_VALUE);
        addCookie(response, cookie);
    }

    private void addCookie(HttpServletResponse response, Cookie cookie) {
        cookie.setDomain("helsinki.fi");
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
