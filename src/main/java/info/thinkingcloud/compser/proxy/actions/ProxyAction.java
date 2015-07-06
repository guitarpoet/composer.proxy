package info.thinkingcloud.compser.proxy.actions;

import info.thinkingcloud.compser.proxy.ResourceNotFoundException;
import info.thinkingcloud.compser.proxy.services.ComposerService;
import info.thinkingcloud.compser.proxy.services.HttpService;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProxyAction {

	private static final Logger logger = LoggerFactory
			.getLogger(ProxyAction.class);

	@Autowired
	private ComposerService pack;

	@RequestMapping("proxy/{repo}/packages.json")
	public void packages(@PathVariable("repo") String repo,
			HttpServletRequest req, PrintWriter out) {
		processResult("No packages.json file found for repo " + repo + "!",
				out, pack.getPackages(repo, req.getContextPath()));
	}

	private void processResult(String message, PrintWriter out, String result) {
		if (result != null && !HttpService.NOT_FOUND.equals(result)) {
			logger.debug("Trying to output result {}.", result);
			out.print(result);
			out.close();
		} else {
			throw new ResourceNotFoundException(message);
		}
	}

	@RequestMapping("proxy/{repo}/search.json")
	public void search(@PathVariable("repo") String repo,
			@RequestParam("q") String query, PrintWriter out) {
		processResult("No query result found for repo " + repo + "!", out,
				pack.search(repo, query));
	}

	@RequestMapping("proxy/{repo}/p/{file}")
	public void providers(@PathVariable("repo") String repo,
			@PathVariable("file") String file, PrintWriter out) {
		processResult("No file is found for repo " + repo + "!", out,
				pack.getFile(repo, file));
	}

	@RequestMapping("dist/{repo}/p/{file}")
	public void file(@PathVariable("repo") String repo,
			@PathVariable("file") String file, PrintWriter out) {
		processResult("No file is found for repo " + repo + "!", out,
				pack.getFile(repo, file));
	}

	@RequestMapping("dist/{repo}/p/{package}/{file}")
	public void details(@PathVariable("repo") String repo,
			@PathVariable("package") String packages,
			@PathVariable("file") String file, PrintWriter out) {
		processResult("No file is found for repo " + repo + "!", out,
				pack.getFile(repo, packages + "/" + file));
	}
}
