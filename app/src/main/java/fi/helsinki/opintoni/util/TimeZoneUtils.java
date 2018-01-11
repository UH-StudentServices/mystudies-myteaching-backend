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

package fi.helsinki.opintoni.util;

import fi.helsinki.opintoni.cache.CacheConstants;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VTimeZone;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class TimeZoneUtils {
    private final String EUROPE_HELSINKI_TIMEZONE_ID = "Europe/Helsinki";
    private final TimeZoneRegistry timeZoneRegistry;

    public TimeZoneUtils() {
       this.timeZoneRegistry =  TimeZoneRegistryFactory.getInstance().createRegistry();
    }

    @Cacheable(value = CacheConstants.EUROPE_HELSINKI_TIMEZONE, cacheManager = "transientCacheManager")
    public VTimeZone getHelsinkiTimeZone() {
        return timeZoneRegistry.getTimeZone(EUROPE_HELSINKI_TIMEZONE_ID).getVTimeZone();
    }
}
