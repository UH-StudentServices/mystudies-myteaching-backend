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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/*
    This API is used by other services. Do not delete!
 */
@RestController
@RequestMapping(RestConstants.PUBLIC_API_V1)
public class SystemResource {

    private static final Logger logger = LoggerFactory.getLogger(SystemResource.class);
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
        boolean hasConnection = false;
        RedisConnection redisConnection = null;
        try {
            redisConnection = RedisConnectionUtils.getConnection(this.redisConnectionFactory);
            hasConnection = !redisConnection.isClosed();
        } catch (Exception e) {
            logger.error(REDIS_ERROR_MESSAGE, e);
        } finally {
            if (redisConnection != null) {
                redisConnection.close();
            }
        }
        return hasConnection;
    }

    private boolean hasDatabaseConnection() {

        boolean hasConnection = false;

        try (Connection databaseConnection = this.dataSource.getConnection()) {
            hasConnection = databaseConnection.isValid(DATABASE_CHECK_TIMEOUT);
        } catch (SQLException e) {
            logger.error(DATABASE_ERROR_MESSAGE, e);
        }
        return hasConnection;

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
