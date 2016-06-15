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

package fi.helsinki.opintoni.web.rest.publicapi;

import fi.helsinki.opintoni.config.AppConfiguration;
import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = RestConstants.PUBLIC_API_V1, produces = WebConstants.APPLICATION_JSON_UTF8)
public class ConfigurationResource extends AbstractResource {

    private final String configuration;

    @Autowired
    public ConfigurationResource(AppConfiguration appConfiguration) throws Exception {
        configuration = getConfiguration(appConfiguration);
    }

    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    public String getConfiguration() throws JSONException {
        return configuration;
    }

    private String getConfiguration(AppConfiguration appConfiguration) throws JSONException {
        JSONObject configuration = new JSONObject();
        configuration.put("googleAnalyticsAccount", appConfiguration.get("googleAnalyticsAccount"));
        configuration.put("loginUrlTeacher", appConfiguration.get("loginUrlTeacher"));
        configuration.put("loginUrlStudent", appConfiguration.get("loginUrlStudent"));
        configuration.put("logoutUrl", appConfiguration.get("logoutUrl"));
        configuration.put("embedlyApiKey", appConfiguration.get("embedlyApiKey"));
        configuration.put("studentAppUrl", appConfiguration.get("studentAppUrl"));
        configuration.put("teacherAppUrl", appConfiguration.get("teacherAppUrl"));
        configuration.put("environment", appConfiguration.get("environment"));

        return configuration.toString();
    }
}
