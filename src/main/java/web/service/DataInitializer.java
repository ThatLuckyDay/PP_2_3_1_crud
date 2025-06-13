package web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import web.dao.UserDao;
import web.model.Role;
import web.model.User;

import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        User adminUser = new User(null, "admin", "admin", "admin@site.ru",
                "admin", passwordEncoder.encode("admin"),
                Set.of(new Role(null, "ROLE_ADMIN")));
        
        User simpleUser = new User(null, "user", "user", "user@site.ru",
                "user", passwordEncoder.encode("user"),
                Set.of(new Role(null, "ROLE_USER")));

        userDao.initDatabase(List.of(adminUser, simpleUser));
    }
}
