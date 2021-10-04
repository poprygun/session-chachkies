package io.microsamples.sessions.sessionchachkies;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableWebMvc
public class SessionChachkiesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SessionChachkiesApplication.class, args);
	}

}
@Controller
class SpringSessionJdbcController {

	@GetMapping("/")
	public String index(Model model, HttpSession session) {
		List<Chachkie> chachkies = getChachkies(session);
		model.addAttribute("data", chachkies);
		model.addAttribute("sessionId", session.getId());
		return "index";
	}

	@PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String saveMessage
			(@RequestBody Chachkie chachkie, HttpServletRequest request) {

		List<Chachkie> chachkies = getChachkies(request.getSession());

		if (!chachkies.contains(chachkie)) {
			chachkies.add(chachkie);
			request.getSession().
					setAttribute("data", chachkies);
		}
		return "redirect:/";
	}

	private List<Chachkie> getChachkies(HttpSession session) {
		List<Chachkie> data = (List<Chachkie>) session.getAttribute("data");

		if (data == null) {
			data = new ArrayList<>();
		}
		return data;
	}
}

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
class Chachkie implements Serializable {
	UUID id;
	Double latitude;
	Double longitude;
}