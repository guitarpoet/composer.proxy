package info.thinkingcloud.compser.proxy.services;

import javax.annotation.PostConstruct;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The caching service that will save the files into the cache.
 * 
 * @author jack
 */
@Service
public class CacheService {

	private static final String FILES = "files";

	private static final String PACKAGES = "packages";

	private CacheManager manager;

	@Autowired
	private ConfigService config;

	@PostConstruct
	public void init() {
		Configuration cacheManagerConfig = new Configuration()
				.diskStore(new DiskStoreConfiguration().path(config
						.getConfig("cache.dir")));
		CacheConfiguration packages = new CacheConfiguration()
				.name(PACKAGES)
				.maxBytesLocalHeap(16, MemoryUnit.MEGABYTES)
				.persistence(
						new PersistenceConfiguration()
								.strategy(Strategy.LOCALTEMPSWAP));

		CacheConfiguration files = new CacheConfiguration()
				.name(FILES)
				.maxBytesLocalHeap(16, MemoryUnit.MEGABYTES)
				.persistence(
						new PersistenceConfiguration()
								.strategy(Strategy.LOCALTEMPSWAP));

		cacheManagerConfig.addCache(packages);
		cacheManagerConfig.addCache(files);

		manager = new CacheManager(cacheManagerConfig);
	}

	public Cache getPackagesCache() {
		return manager.getCache(PACKAGES);
	}

	public Cache getFilesCache() {
		return manager.getCache(FILES);
	}
}
