package info.thinkingcloud.compser.proxy.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HttpService {
	private static final Logger logger = LoggerFactory
			.getLogger(HttpService.class);

	private HttpClient client;

	@PostConstruct
	public void init() {
		client = HttpClientBuilder
				.create()
				.setDefaultRequestConfig(
						RequestConfig.custom().setRedirectsEnabled(true)
								.setRelativeRedirectsAllowed(true).build())
				.setRedirectStrategy(new LaxRedirectStrategy())
				.setConnectionManager(new PoolingHttpClientConnectionManager())
				.build();
	}

	public String get(String uri, HashMap<String, Object> parameters) {
		try {
			URIBuilder builder = new URIBuilder(uri);
			if (parameters != null) {
				for (Map.Entry<String, Object> e : parameters.entrySet()) {
					builder.addParameter(e.getKey(),
							String.valueOf(e.getValue()));
				}
			}

			HttpGet get = new HttpGet(builder.build());

			HttpResponse resp = client.execute(get);
			if (resp.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(resp.getEntity());
			} else {
				logger.error("Error in fetching uri {} with parameters {}",
						uri, parameters);
			}
		} catch (URISyntaxException exc) {
			logger.error("Error in building uri to request...", exc);
		} catch (ClientProtocolException exc) {
			logger.error("Error in building http request", exc);
		} catch (IOException exc) {
			logger.error("Error in getting response", exc);
		}
		return null;
	}
}
