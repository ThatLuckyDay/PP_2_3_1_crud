package web.service;

import web.model.Role;
import web.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    void add(User user);

    User update(User user);

    void delete(User user);

    List<User> listUsers();

    User getUserById(Long id);

    Set<Role> listRoles();

    void addRoleToUser(User user, Role role);

    Set<Role> getRolesByIds(List<Long> roleIds);

    User getCurrentUser();
}
