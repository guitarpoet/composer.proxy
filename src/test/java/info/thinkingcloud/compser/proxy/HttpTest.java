package info.thinkingcloud.compser.proxy;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import info.thinkingcloud.compser.proxy.services.HttpService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "file:./src/main/webapp/WEB-INF/server-servlet.xml" })
public class HttpTest {

	@Autowired
	private HttpService http;

	@Test
	public void testGetRedirect() {
		assertThat(http, notNullValue());
		System.out
				.println(http
						.get("http://packagist.org/p/provider-2013$ba307417cd9bf0f23a3c8f584b4cc68ec45bf5b2b1d76ce608203e75d35d38cd",
								null));
	}
}
