package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/userDetails/{id}")
	public String getUserDetails(@PathVariable("id") Long id, Model model) {
		User user = this.userService.getUser(id);
		if(user!=null) {
			model.addAttribute("user", user);
			return "userDetails.html";
		} else {
			return "resourceNotFound.html";
		}
		
	}
}
