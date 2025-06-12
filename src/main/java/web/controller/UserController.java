package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.Set;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/user")
    public String showUserPage(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "index";
    }

    @GetMapping("/admin")
    public String showAdminPage(@AuthenticationPrincipal User principal, Model model) {
        model.addAttribute("principal", principal);
        model.addAttribute("users", userService.listUsers());
        model.addAttribute("roles", userService.listRoles());

        model.addAttribute("formUser", new User());

        return "index";
    }

    @PostMapping("/admin")
    public String addUser(@RequestParam(required = false) String newRole,
            @RequestParam(name = "roleIds", required = false) List<Long> roleIds, @ModelAttribute("user") User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = userService.getRolesByIds(roleIds);
            user.setRoles(roles);
        }

        if (newRole != null && !newRole.isBlank()) {
            userService.addRoleToUser(user, new Role(null, newRole));
        }
        userService.update(user);
        return "redirect:/index";
    }

    @GetMapping("/admin/edit")
    public String editUser(@RequestParam Long id, Model model) {
        User user = userService.getUserById(id);
        user.setPassword("");
        model.addAttribute("user", user);
        model.addAttribute("roles", userService.listRoles());

        return "index";
    }

    @GetMapping("/admin/delete")
    public String deleteUser(@RequestParam Long id, Model model) {
        User user = userService.getUserById(id);
        user.setPassword("");
        model.addAttribute("formUser", user);
//        userService.delete(userService.getUserById(id));
        return "redirect:/index";
    }
}