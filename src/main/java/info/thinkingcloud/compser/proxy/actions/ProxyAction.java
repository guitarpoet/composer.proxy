package info.thinkingcloud.compser.proxy.actions;

import info.thinkingcloud.compser.proxy.ResourceNotFoundException;
import info.thinkingcloud.compser.proxy.services.ComposerService;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProxyAction {

	@Autowired
	private ComposerService pack;

	@RequestMapping("proxy/{repo}/packages.json")
	public void packages(@PathVariable("repo") String repo,
			HttpServletRequest req, PrintWriter out) {
		processResult("No packages.json file found for repo " + repo + "!",
				out, pack.getPackages(repo, req.getContextPath()));
	}

	private void processResult(String message, PrintWriter out, String result) {
		if (result != null) {
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
}
