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

import fi.helsinki.opintoni.config.Constants;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.integration.oodi.OodiIntegrationException;
import fi.helsinki.opintoni.service.TimeService;
import fi.helsinki.opintoni.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public abstract class BaseAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private UserService userService;

    private TimeService timeService;

    private Environment env;

    @Autowired
    public void initialize(UserService userService, TimeService timeService, Environment env) {
        this.userService = userService;
        this.timeService = timeService;
        this.env = env;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        AppUser appUser = (AppUser) authentication.getPrincipal();

        try {
            syncUserWithDatabase(appUser);

            if (!hasLanguageCookie(request)) {
                addLanguageCookie(appUser, response);
            }

            if(!env.acceptsProfiles(Constants.SPRING_PROFILE_DEMO)) {
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

    private boolean hasLanguageCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        return cookies != null && Arrays.stream(cookies)
            .anyMatch(cookie -> Constants.NG_TRANSLATE_LANG_KEY.equals(cookie.getName()));
    }

    private void addLanguageCookie(AppUser appUser, HttpServletResponse response) {
        Cookie cookie = new Cookie(Constants.NG_TRANSLATE_LANG_KEY, "%22" + appUser.getPreferredLanguage() + "%22");
        addCookie(response, cookie);
    }

    // NOTE: This cookie is relied on by courses.helsinki.fi for automatic user login
    // and should thus not be removed without consulting the course page team first.
    private void addHasLoggedInCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(Constants.OPINTONI_HAS_LOGGED_IN, Boolean.TRUE.toString());
        cookie.setMaxAge(Integer.MAX_VALUE);
        addCookie(response, cookie);
    }

    private void addCookie(HttpServletResponse response, Cookie cookie) {
        cookie.setDomain("helsinki.fi");
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
