package info.thinkingcloud.compser.proxy.actions;

import java.io.PrintWriter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeAction {
	@RequestMapping("/")
	public void index(PrintWriter out) {
		out.println("Hello");
	}
}
