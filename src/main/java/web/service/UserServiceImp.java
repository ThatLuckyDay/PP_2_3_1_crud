package web.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import web.dao.RoleDao;
import web.dao.UserDao;
import web.model.Role;
import web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void add(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.add(user);
    }

    @Override
    @Transactional
    public User update(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRoles(user.getRoles()
                .stream()
                .map(role -> roleDao.findByAuthority(role.getAuthority()).orElseThrow())
                .collect(Collectors.toSet())
        );
        User updatedUser = userDao.update(user);

        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (updatedUser.getId().equals(((User) currentAuth.getPrincipal()).getId())) {
            UsernamePasswordAuthenticationToken newAuth =
                    new UsernamePasswordAuthenticationToken(
                            updatedUser,
                            currentAuth.getCredentials(),
                            updatedUser.getAuthorities()
                    );
            newAuth.setDetails(currentAuth.getDetails());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        return updatedUser;
    }

    @Override
    @Transactional
    public void delete(User user) {
        userDao.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listUsers() {
        return userDao.listUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userDao.getUserById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> listRoles() {
        return roleDao.getRoles();
    }

    @Override
    @Transactional
    public void addRoleToUser(User user, Role role) {
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        final String authority = role.getAuthority().toUpperCase();

        final String prefixedRole = authority.startsWith("ROLE_")
                ? authority
                : "ROLE_" + authority;

        boolean roleExists = user.getRoles().stream()
                .anyMatch(r -> r.getAuthority().equals(prefixedRole));

        if (!roleExists) {
            Role dbRole = roleDao.findByAuthority(prefixedRole)
                    .orElseGet(() -> roleDao.save(new Role(null, prefixedRole)));

            user.getRoles().add(dbRole);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> getRolesByIds(List<Long> roleIds) {
        return roleDao.findRolesByIds(roleIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getByUsername(String email) {
        return userDao.getByUsername(email);
    }
}