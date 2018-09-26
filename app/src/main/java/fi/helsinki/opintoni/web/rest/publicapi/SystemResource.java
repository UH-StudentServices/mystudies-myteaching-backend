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

import fi.helsinki.opintoni.web.WebConstants;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

/*
    This API is used by other services. Do not delete!
 */
@RestController
@RequestMapping(RestConstants.PUBLIC_API_V1)
public class SystemResource {

    private static final String OK_STATUS_MESSAGE = "ok";
    private static final String REDIS_ERROR_MESSAGE = "Redis connection failed";
    private static final String DATABASE_ERROR_MESSAGE = "Database connection failed";
    private static final int DATABASE_CHECK_TIMEOUT = 1;

    private final RedisConnectionFactory redisConnectionFactory;
    private final DataSource dataSource;

    @Autowired
    public SystemResource(RedisConnectionFactory redisConnectionFactory, DataSource dataSource) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.dataSource = dataSource;
    }

    @GetMapping(
        value = "/health-check",
        produces = WebConstants.APPLICATION_JSON_UTF8)
    public ResponseEntity<HealthStatus> getHealthStatus() {
        if (!hasRedisConnection()) {
            return new ResponseEntity<>(new HealthStatus(REDIS_ERROR_MESSAGE), HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (!hasDatabaseConnection()) {
            return new ResponseEntity<>(new HealthStatus(DATABASE_ERROR_MESSAGE), HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<>(new HealthStatus(OK_STATUS_MESSAGE), HttpStatus.OK);
    }

    private boolean hasRedisConnection() {
        try {
            RedisConnectionUtils.getConnection(this.redisConnectionFactory);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasDatabaseConnection() {
        try {
            return this.dataSource.getConnection().isValid(DATABASE_CHECK_TIMEOUT);
        } catch (Exception e) {
            return false;
        }
    }

    public static class HealthStatus {

        String message;

        HealthStatus(String message) {
            this.message = message;
        }

        public String getStatus() {
            return message;
        }

    }
}
