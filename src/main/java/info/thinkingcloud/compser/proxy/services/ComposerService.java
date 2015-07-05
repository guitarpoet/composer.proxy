package info.thinkingcloud.compser.proxy.services;

import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map.Entry;

import net.sf.ehcache.Element;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComposerService {
	private static final Logger logger = LoggerFactory
			.getLogger(ComposerService.class);

	@Autowired
	private HttpService http;

	@Autowired
	private ConfigService config;

	@Autowired
	private CacheService cache;

	public String search(String repo, String query) {
		String uri = config.getConfig(repo);
		String md5 = repo + query;
		try {
			md5 = getMD5(md5);
			logger.debug(
					"Trying to get query result for repo {}, uri {} and query {} with md5 key {}",
					new Object[] { repo, uri, query, md5 });
			if (uri != null) {
				Element e = cache.getFilesCache().get(md5);
				if (e != null)
					return e.getObjectValue().toString();
				HashMap<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("q", query);
				String result = http.get(uri + "/search.json", parameters);
				cache.getFilesCache().put(new Element(md5, result));
				return result;
			}
		} catch (NoSuchAlgorithmException exc) {
			logger.error("You don't have md5 algorithm!", exc);
		}
		return null;
	}

	private String getMD5(String md5) throws NoSuchAlgorithmException {
		return new String(MessageDigest.getInstance("MD5").digest(
				md5.getBytes()));
	}

	public String getFile(String repo, String path) {
		String uri = config.getConfig(repo);
		try {
			String md5 = getMD5(repo + "/p/" + path);
			if (uri != null) {
				Element e = cache.getFilesCache().get(repo);
				if (e != null) {
					return e.getObjectValue().toString();
				}
				logger.debug(
						"Trying to fetch file from repo {} with uri {} and path {}",
						new Object[] { repo, uri, path });
				String result = http.get(uri + "/p/" + path, null);
				cache.getFilesCache().put(new Element(md5, result));
				return result;
			}
		} catch (NoSuchAlgorithmException e1) {
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public String getPackages(String repo, String context) {
		String uri = config.getConfig(repo);
		if (uri != null) {
			Element e = cache.getPackagesCache().get(repo);
			if (e != null) {
				return e.getObjectValue().toString();
			}
			logger.debug(
					"Trying to fetch packages.json from repo {} with uri {}",
					repo, uri);
			String result = http.get(uri + "/packages.json", null);
			JSONObject json = JSONObject.fromObject(result);

			for (Object o : json.entrySet()) {
				@SuppressWarnings("rawtypes")
				Entry en = (Entry) o;
				if (en.getValue() instanceof String) {
					en.setValue(context + "/proxy/packagist" + en.getValue());
				}
			}
			StringWriter out = new StringWriter();
			json.write(out);
			cache.getPackagesCache().put(new Element(repo, out.toString()));
			return out.toString();
		}
		return null;
	}
}
