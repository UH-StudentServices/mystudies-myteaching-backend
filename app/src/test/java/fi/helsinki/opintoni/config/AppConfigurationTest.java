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

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

public class AppConfigurationTest {

    private AppConfiguration appConfiguration;

    @Before
    public void setUp() {
        appConfiguration = new AppConfiguration(
            new MockEnvironment()
                .withProperty("url", "http://www.mysite.com")
                .withProperty("enabled", "true"));

        appConfiguration.override("url", "http://www.radiogaga.com");
    }

    @Test
    public void getProperty() {
        assertThat(appConfiguration.get("enabled")).isEqualTo("false");
    }

    @Test
    public void overrideProperty() {
        assertThat(appConfiguration.get("url")).isEqualTo("http://www.radiogaga.com");
    }

    @Test
    public void resetProperty() {
        appConfiguration.reset("url");
        assertThat(appConfiguration.get("url")).isEqualTo("http://www.mysite.com");
    }

    @Test
    public void resetAllProperties() {
        appConfiguration.resetAll();
        assertThat(appConfiguration.get("url")).isEqualTo("http://www.mysite.com");
        assertThat(appConfiguration.get("enabled")).isEqualTo("true");
    }
}
