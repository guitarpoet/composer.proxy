package info.thinkingcloud.compser.proxy.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {
	private static final Logger logger = LoggerFactory
			.getLogger(ConfigService.class);

	private HashMap<String, Object> config = new HashMap<String, Object>();

	public Set<Entry<String, Object>> entrySet() {
		return config.entrySet();
	}

	@Autowired
	private ApplicationContext context;

	public String config(String config) {
		return getConfig(config);
	}

	public String config(String config, String defaultValue) {
		String ret = config(config);
		if (ret == null)
			return defaultValue;
		return ret;
	}

	public String getConfig(String key) {
		return getConfig(key, String.class);
	}

	@SuppressWarnings("unchecked")
	public <T> T getConfig(String key, Class<T> clazz) {
		return (T) config.get(key);
	}

	public void setConfig(String key, Object value) {
		config.put(key, value);
	}

	public String getDefaultIndexTarget() {
		return SystemUtils.getUserDir().getAbsolutePath();
	}

	public String getDefaultIndexDest() {
		return FilenameUtils.concat(SystemUtils.getUserDir().getAbsolutePath(),
				".index");
	}

	@PostConstruct
	public void init() throws IOException {
		// Trying to locate configuration file
		String[] paths = {
				"/etc/composer",
				FilenameUtils.concat(SystemUtils.getUserHome()
						.getAbsolutePath(), ".composer"),
				SystemUtils.getUserDir().getAbsolutePath() };

		// Load all the system properties to configuration
		loadPropertiesToConfig(System.getProperties());

		// Load the default configuration
		Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("config/default.properties"));

		loadPropertiesToConfig(properties);
		for (String path : paths) {
			try {

				loadConfig(path);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private void loadConfig(String path) throws FileNotFoundException,
			IOException {
		File f = new File(FilenameUtils.concat(path, "config.properties"));
		if (f.exists()) {
			Properties p = new Properties();
			p.load(new FileReader(f));
			loadPropertiesToConfig(p);
		}
	}

	private void loadPropertiesToConfig(Properties properties) {
		for (Map.Entry<Object, Object> e : properties.entrySet()) {
			config.put(String.valueOf(e.getKey()), e.getValue());
		}
	}
}
