package web.dao;

import web.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
   void add(User user);
   List<User> listUsers();

   User update(User user);

   void delete(User user);

   User getUserById(Long id);

   Optional<User> getByUsername(String username);

   void initDatabase();
}
