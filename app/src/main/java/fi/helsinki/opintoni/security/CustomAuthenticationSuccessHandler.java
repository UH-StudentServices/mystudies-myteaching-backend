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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private TimeService timeService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        AppUser appUser = (AppUser) authentication.getPrincipal();

        try {
            syncUserWithDatabase(appUser);

            addLanguageCookie(appUser, response);
            addLastLoginCookie(response);
            addHasLoggedInCookie(response);

            response.sendRedirect("/");

        } catch (OodiIntegrationException e) {
            response.sendRedirect("/error/maintenance");
        }

    }

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

    private void addLanguageCookie(AppUser appUser, HttpServletResponse response) {
        Cookie cookie = new Cookie(Constants.NG_TRANSLATE_LANG_KEY, "%22" + appUser.getPreferredLanguage() + "%22");
        addCookie(response, cookie);
    }

    private void addLastLoginCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(Constants.OPINTONI_LAST_LOGIN, timeService.nowUTCAsString());
        addCookie(response, cookie);
    }

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
