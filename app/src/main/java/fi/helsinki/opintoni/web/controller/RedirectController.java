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

package fi.helsinki.opintoni.web.controller;

import fi.helsinki.opintoni.config.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class RedirectController {

    private final AppConfiguration appConfiguration;

    private static final String STATE_STUDENT = "opintoni";
    private static final String STATE_TEACHER = "opetukseni";

    private static final String REDIRECT_STUDENT = "/app/#/opintoni";
    private static final String REDIRECT_TEACHER = "/app/#/opetukseni";
    private static final String REDIRECT_DEFAULT = "/app";

    @Autowired
    public RedirectController(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @RequestMapping(value = "/redirect")
    public String redirect(@RequestParam("state") String state) {
        switch (state) {
            case STATE_STUDENT:
                return "redirect:" + getRedirectPath("studentAppUrl", REDIRECT_STUDENT);
            case STATE_TEACHER:
                return "redirect:" + getRedirectPath("teacherAppUrl", REDIRECT_TEACHER);
            default:
                return "redirect:" + REDIRECT_DEFAULT;
        }
    }

    private String getRedirectPath(String configurationParam, String defaultRedirect) {
        return Optional
            .ofNullable(appConfiguration.get(configurationParam))
            .filter(p -> p.length() > 0)
            .orElseGet(() -> defaultRedirect);
    }
}
