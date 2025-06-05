package web.service;

import web.model.User;

import java.util.List;

public interface UserService {
    void add(User user);

    User update(User user);

    void delete(User user);

    List<User> listUsers();

    User getUserById(Long id);
}
