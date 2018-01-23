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

package fi.helsinki.opintoni.config.locale;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Angular cookie saved the locale with a double quotes (%22en%22).
 * The default CookieLocaleResolver#StringUtils.parseLocaleString(localePart)
 * is not able to parse the locale.
 */
public class AngularCookieLocaleResolver extends CookieLocaleResolver {

    private final List<String> availableLanguages;

    public AngularCookieLocaleResolver(String cookieName, String defaultLanguage, List<String> availableLanguages) {
        setCookieName(cookieName);
        setDefaultLocale(new Locale(defaultLanguage));

        this.availableLanguages = availableLanguages;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        parseLocaleCookieIfNecessary(request);
        return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
    }

    @Override
    public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
        parseLocaleCookieIfNecessary(request);
        return () -> (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
    }

    private void parseLocaleCookieIfNecessary(HttpServletRequest request) {
        if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
            Locale locale = Optional.ofNullable(WebUtils.getCookie(request, getCookieName()))
                .map(Cookie::getValue)
                .map(value -> StringUtils.replace(value, "%22", ""))
                .filter(availableLocales::contains)
                .map(StringUtils::parseLocaleString)
                .orElse(determineDefaultLocale(request));

            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
        }
    }
}
