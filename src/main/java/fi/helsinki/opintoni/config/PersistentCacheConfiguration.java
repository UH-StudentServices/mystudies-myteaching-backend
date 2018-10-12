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

import fi.helsinki.opintoni.cache.CacheConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
@EnableCaching
public class PersistentCacheConfiguration extends CachingConfigurerSupport {

    private static final String DEFAULT_REDIS_HOST = "localhost";
    private static final int DEFAULT_REDIS_PORT = 6379;
    private static final long DEFAULT_CACHE_EXPIRATION = 0;

    @Autowired
    private CacheManager persistentCacheManager;

    @Autowired
    private Environment environment;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    private static class CustomCacheErrorHandler extends SimpleCacheErrorHandler {

        private final Logger log = LoggerFactory.getLogger(CustomCacheErrorHandler.class);

        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
            log.error("Caught exception when trying to get from cache", exception);
        }

        @Override
        public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
            log.error("Caught exception when trying to put into cache", exception);
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
            log.error("Caught exception when trying to evict from cache", exception);
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, Cache cache) {
            log.error("Caught exception when trying to clear cache", exception);
        }
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        String redistHost = environment.getProperty("redis.host", DEFAULT_REDIS_HOST);
        int redisPort = environment.getProperty("redis.port", Integer.class, DEFAULT_REDIS_PORT);
        String redisPassword = environment.getProperty("redis.password");

        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(redistHost, redisPort);
        standaloneConfiguration.setPassword(RedisPassword.of(redisPassword));

        return new JedisConnectionFactory(standaloneConfiguration);
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }

    @Bean("persistentCacheManager")
    @Override
    public CacheManager cacheManager() {
        long defaultCacheExpiration = environment.getProperty("redis.defaultCacheExpirationSeconds", Long.class, DEFAULT_CACHE_EXPIRATION);

        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();

        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration
            .defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(defaultCacheExpiration));

        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory)
            .cacheDefaults(cacheConfiguration)
            .initialCacheNames(CacheConstants.PERSISTENT_CACHE_NAMES)
            .build();
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return new SimpleCacheResolver(persistentCacheManager);
    }
}
