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

package fi.helsinki.opintoni.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@PropertySource(value = {
    "file:/opt/opintoni/config/default.properties",
    "file:/opt/opintoni/config/network.properties",
    "file:/opt/opintoni/config/database.properties",
    "file:/opt/opintoni/config/credentials.properties",
    "file:/opt/opintoni/config/esb.properties"},
    ignoreResourceNotFound = true)
public class AppConfiguration {

    private final Environment environment;

    private final Map<String, String> runtimePropertyOverrides = new HashMap<>();

    @Autowired
    public AppConfiguration(Environment environment) {
        this.environment = environment;
    }

    public String get(String key) {
        return Optional.ofNullable(runtimePropertyOverrides.get(key))
            .orElseGet(() -> environment.getProperty(key));
    }

    public List<Integer> getIntegerValues(String key) {
        return Lists.newArrayList(environment.getProperty(key, Integer[].class));
    }

    public List<String> getStringValues(String key) {
        return Lists.newArrayList(environment.getProperty(key, String[].class));
    }

    public int getInteger(String key) {
        return Integer.valueOf(get(key));
    }

    public void override(String key, String value) {
        runtimePropertyOverrides.put(key, value);
    }

    public void reset(String key) {
        runtimePropertyOverrides.remove(key);
    }

    public void resetAll() {
        runtimePropertyOverrides.clear();
    }
}
