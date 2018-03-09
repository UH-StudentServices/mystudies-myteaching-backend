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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ehcache.InstrumentedEhcache;
import fi.helsinki.opintoni.cache.CacheConstants;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.SortedSet;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = {MetricsConfiguration.class, DatabaseConfiguration.class})
public class TransientCacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(TransientCacheConfiguration.class);

    @Inject
    private Environment env;

    @Inject
    private MetricRegistry metricRegistry;

    private net.sf.ehcache.CacheManager cacheManager;

    @PreDestroy
    public void destroy() {
        log.debug("Remove Cache Manager metrics");
        SortedSet<String> names = metricRegistry.getNames();
        names.forEach(metricRegistry::remove);
        log.debug("Closing Cache Manager");
        cacheManager.shutdown();
    }

    @Bean(name = "transientCacheManager")
    public CacheManager cacheManager() {
        log.debug("Starting Ehcache");

        cacheManager = net.sf.ehcache.CacheManager.create();
        configureCaches();

        EhCacheCacheManager ehCacheManager = new EhCacheCacheManager();
        ehCacheManager.setCacheManager(cacheManager);
        return ehCacheManager;
    }

    private void configureCaches() {
        CacheConstants.TRANSIENT_CACHE_NAMES.forEach(cacheName -> {
            cacheManager.addCache(cacheName);
            setDefaultCacheProperties(cacheName);
        });
    }

    private void setDefaultCacheProperties(final String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        net.sf.ehcache.config.CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();

        String timeToLiveKey = "cache." + cacheName + ".timeToLiveSeconds";
        cacheConfiguration.setTimeToLiveSeconds(env.getProperty(timeToLiveKey, Long.class, 3600L));

        String maxEntriesLocalHeap = "cache." + cacheName + ".maxEntriesLocalHeap";
        cacheConfiguration.setMaxEntriesLocalHeap(env.getProperty(maxEntriesLocalHeap, Long.class, 100L));

        cacheConfiguration.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));

        Ehcache decoratedCache = InstrumentedEhcache.instrument(metricRegistry, cache);
        cacheManager.replaceCacheWithDecoratedCache(cache, decoratedCache);
    }
}
