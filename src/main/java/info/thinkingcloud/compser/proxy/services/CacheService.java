package info.thinkingcloud.compser.proxy.services;

import java.util.List;

import info.thinkingcloud.compser.proxy.entities.CacheItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The caching service that will save the files into the cache.
 * 
 * @author jack
 */
@Service
public class CacheService {

	@Autowired
	private HibernateTemplate template;

	@Transactional
	public void put(String cache, String key, String value) {
		CacheItem item = getItem(cache, key);
		if (item != null) {
			item.setValue(value);
			template.update(item);
		} else {
			item = new CacheItem(cache, key, value);
			template.save(item);
		}

	}

	protected CacheItem getItem(String cache, String key) {
		List<?> list = template.find(
				"from CacheItem where cache = ? and name = ?", cache, key);
		if (list.isEmpty())
			return null;
		return (CacheItem) list.get(0);
	}

	public String get(String cache, String key) {
		CacheItem item = getItem(cache, key);
		if (item != null)
			return item.getValue();
		return null;
	}
}
