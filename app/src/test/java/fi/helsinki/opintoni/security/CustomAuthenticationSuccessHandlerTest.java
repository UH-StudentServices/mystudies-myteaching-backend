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

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.helsinki.opintoni.config.Constants;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.security.enumerated.SAMLEduPersonAffiliation;
import fi.helsinki.opintoni.service.SessionService;
import fi.helsinki.opintoni.service.TimeService;
import fi.helsinki.opintoni.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomAuthenticationSuccessHandlerTest {

    private static final String EDU_PRINCIPAL_NAME = "eduPrincipalName";
    private static final String OODI_PERSON_ID = "oodiPersonId";

    private final Authentication authentication = mock(Authentication.class);

    @Mock
    private ObjectMapper mapper;

    @Mock
    private UserService userService;

    @Mock
    private SessionService sessionService;

    @Mock
    private TimeService timeService;

    @InjectMocks
    private CustomAuthenticationSuccessHandler handler;

    @Before
    public void init() {
        AppUser appUser = new AppUser.AppUserBuilder()
            .eduPersonPrincipalName(EDU_PRINCIPAL_NAME)
            .studentNumber("1234")
            .oodiPersonId(OODI_PERSON_ID)
            .preferredLanguage("fi")
            .eduPersonAffiliations(Arrays.asList(SAMLEduPersonAffiliation.STUDENT))
            .build();
        when(authentication.getPrincipal()).thenReturn(appUser);
    }

    @Test
    public void thatOldUserDoesNotResultInNewUser() throws Exception {
        Optional<User> user = Optional.of(new User());
        HttpServletResponse response = mockResponse();

        when(userService.findFirstByEduPersonPrincipalName(EDU_PRINCIPAL_NAME)).thenReturn(user);

        handler.onAuthenticationSuccess(mock(HttpServletRequest.class), response, authentication);

        verify(userService, never()).createNewUser(any(AppUser.class));
    }

    @Test
    public void thatMissingOodiPersonIdIsUpdated() throws Exception {
        Optional<User> user = Optional.of(new User());
        when(userService.findFirstByEduPersonPrincipalName(EDU_PRINCIPAL_NAME)).thenReturn(user);

        handler.onAuthenticationSuccess(mock(HttpServletRequest.class), mockResponse(), authentication);

        verify(userService, times(1)).save(argThat(new UserMatcher()));
    }

    @Test
    public void thatNewUserIsSaved() throws Exception {
        when(userService.findFirstByEduPersonPrincipalName(EDU_PRINCIPAL_NAME)).thenReturn(Optional.empty());

        handler.onAuthenticationSuccess(mock(HttpServletRequest.class), mockResponse(), authentication);

        verify(userService, times(1)).createNewUser(any(AppUser.class));
    }

    @Test
    public void thatLanguageCookieIsAdded() throws Exception {
        HttpServletResponse response = mockResponse();
        when(userService.findFirstByEduPersonPrincipalName(EDU_PRINCIPAL_NAME)).thenReturn(Optional.empty());

        handler.onAuthenticationSuccess(mock(HttpServletRequest.class), response, authentication);

        verify(response, times(1)).addCookie(argThat(new LangCookieMatcher()));
    }

    @Test
    public void thatLastLoginCookieIsAdded() throws Exception {
        HttpServletResponse response = mockResponse();
        when(userService.findFirstByEduPersonPrincipalName(EDU_PRINCIPAL_NAME)).thenReturn(Optional.empty());

        handler.onAuthenticationSuccess(mock(HttpServletRequest.class), response, authentication);

        verify(response, times(1)).addCookie(argThat(new LastLoginCookieMatcher()));
    }

    @Test
    public void thatHasLoggedInCookieIsAdded() throws IOException, ServletException {
        HttpServletResponse response = mockResponse();
        when(userService.findFirstByEduPersonPrincipalName(EDU_PRINCIPAL_NAME)).thenReturn(Optional.empty());

        handler.onAuthenticationSuccess(mock(HttpServletRequest.class), response, authentication);

        verify(response, times(1)).addCookie(argThat(new HasLoggedInCookieMatcher()));
    }

    private HttpServletResponse mockResponse() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));
        return response;
    }

    private static class LastLoginCookieMatcher extends ArgumentMatcher<Cookie> {

        @Override
        public boolean matches(Object item) {
            Cookie cookie = (Cookie) item;
            return Constants.OPINTONI_LAST_LOGIN.equals(cookie.getName());
        }
    }

    private static class LangCookieMatcher extends ArgumentMatcher<Cookie> {

        @Override
        public boolean matches(Object item) {
            Cookie cookie = (Cookie) item;
            return Constants.NG_TRANSLATE_LANG_KEY.equals(cookie.getName()) && "%22fi%22".equals(cookie.getValue());
        }
    }

    private static class HasLoggedInCookieMatcher extends ArgumentMatcher<Cookie> {

        @Override
        public boolean matches(Object item) {
            Cookie cookie = (Cookie) item;
            return Constants.OPINTONI_HAS_LOGGED_IN.equals(cookie.getName()) && "true".equals(cookie.getValue());
        }
    }

    private static class UserMatcher extends ArgumentMatcher<User> {

        @Override
        public boolean matches(Object argument) {
            User user = (User) argument;
            return user.oodiPersonId.equals(OODI_PERSON_ID);
        }
    }
}
