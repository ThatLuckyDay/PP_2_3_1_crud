package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.model.Role;
import web.model.User;
import web.service.UserService;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/user")
    public String showUserPage(@AuthenticationPrincipal User principal, Model model) {
        model.addAttribute("principal", principal);
        return "user";
    }

    @GetMapping("/admin")
    public String showAdminPage(@AuthenticationPrincipal User principal, Model model) {
        model.addAttribute("principal", principal);
        model.addAttribute("users", userService.listUsers());
        model.addAttribute("user", new User());

        return "admin";
    }

    @PostMapping("/admin/create")
    public String createUser(@RequestParam List<String> authority, @ModelAttribute("user") User user) {
        authority.forEach(role -> userService.addRoleToUser(user, new Role(null, role)));
        userService.update(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit")
    public String editUser(@RequestParam Long id, Model model) {
        User user = userService.getUserById(id);
        user.setPassword("");
        model.addAttribute("user", user);

        return "/admin/edit";
    }

    @PostMapping("/admin/edit")
    public String editUser(@RequestParam List<String> authority, @ModelAttribute("user") User user) {
        return createUser(authority, user);
    }

    @GetMapping("/admin/delete")
    public String deleteUser(@RequestParam Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "/admin/delete";
    }

    @PostMapping("/admin/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(userService.getUserById(id));
        return "redirect:/admin";
    }
}