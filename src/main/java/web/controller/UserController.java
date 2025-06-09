package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.model.User;
import web.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/user")
	public String showUserPage(Model model) {
		model.addAttribute("users", userService.listUsers());
		return "user";
	}

	@GetMapping("/admin")
	public String showAdminPage(Model model) {
		model.addAttribute("users", userService.listUsers());
		model.addAttribute("user", new User());
		return "admin";
	}

	@PostMapping("/admin")
	public String addUser(@ModelAttribute("user") User user) {
		userService.update(user);
		return "redirect:/admin";
	}

	@GetMapping("/admin/edit")
	public String editUser(@RequestParam Long id, Model model) {
		model.addAttribute("user", userService.getUserById(id));
		model.addAttribute("users", userService.listUsers());
		return "admin";
	}

	@GetMapping("/admin/delete")
	public String deleteUser(@RequestParam Long id) {
		userService.delete(userService.getUserById(id));
		return "redirect:/admin";
	}
}