package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/user")
    public String showUserPage(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "user";
    }

    @GetMapping("/admin")
    public String showAdminPage(Model model) {
        model.addAttribute("users", userService.listUsers());
        model.addAttribute("roles", userService.listRoles());
        model.addAttribute("formUser", new User());
        return "admin";
    }

    @PostMapping("/admin")
    public String addUser(@RequestParam(required = false) String newRole,
            @RequestParam(name = "roleIds") List<Long> roleIds, @ModelAttribute("user") User user) {
        user.setRoles(userService.getRolesByIds(roleIds));

        if (newRole != null && !newRole.isBlank()) {
            userService.addRoleToUser(user, new Role(null, newRole));
        }
        userService.update(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit")
    public String editUser(@RequestParam Long id, Model model) {
        User user = userService.getUserById(id);
        user.setPassword("");
        model.addAttribute("formUser", user);

        model.addAttribute("users", userService.listUsers());
        model.addAttribute("roles", userService.listRoles());

        return "admin";
    }

    @GetMapping("/admin/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(userService.getUserById(id));
        return "redirect:/admin";
    }


}