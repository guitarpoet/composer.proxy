package info.thinkingcloud.compser.proxy.services;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComposerService {
	private static final Logger logger = LoggerFactory
			.getLogger(ComposerService.class);

	public static final String FILE = "FILE";
	public static final String PACKAGES = "PACKAGES";

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
				String result = cache.get(FILE, md5);
				if (result != null)
					return result;
				HashMap<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("q", query);
				result = http.get(uri + "/search.json", parameters);
				cache.put(FILE, md5, result);
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
			String md5 = (repo + "/p/" + path);
			if (uri != null) {
				String result = cache.get(FILE, md5);
				if (result != null)
					return result;
				result = http.get(uri + "/p/" + path, null);
				cache.put(FILE, md5, result);
				return result;
			}
		} catch (Throwable exc) {
		}

		return null;
	}

	public String getPackages(String repo, String context) {
		String uri = config.getConfig(repo);
		if (uri != null) {
			String result = cache.get(PACKAGES, repo);

			if (result != null)
				return result;

			StringWriter out = new StringWriter();
			try {
				IOUtils.copy(
						new InputStreamReader(Thread.currentThread()
								.getContextClassLoader()
								.getResourceAsStream("config/default.json")),
						out);
				JSONObject json = JSONObject.fromObject(out.toString());
				JSONArray mirrors = json.getJSONArray("mirrors");
				JSONObject mirror = new JSONObject();
				mirror.put("dist-url", "http://localhost:8080" + context
						+ "/proxy/" + repo
						+ "/dists/%package%/%version%/%reference%.%type%");
				mirror.put("preferred", true);
				mirror.put("git-url", "/git/%package%/%normalizedUrl%.%type%");
				mirrors.add(mirror);
				json.put("providers-lazy-url", context + "/dist/" + repo
						+ "/p/%package%.json");
				out = new StringWriter();
				json.write(out);
				cache.put(PACKAGES, repo, out.toString());
				return result;
			} catch (IOException e) {
				logger.error("Error reading default...", e);
			}

		}
		return null;
	}
}
