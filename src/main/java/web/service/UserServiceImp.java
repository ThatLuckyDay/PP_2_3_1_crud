package web.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import web.dao.RoleDao;
import web.dao.UserDao;
import web.model.Role;
import web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImp implements UserService {

   @Autowired
   private UserDao userDao;

   @Autowired
   private RoleDao roleDao;

   @Transactional
   @Override
   public void add(User user) {
      userDao.add(user);
   }

   @Transactional
   @Override
   public User update(User user) {
      return userDao.update(user);
   }

   @Transactional
   @Override
   public void delete(User user) {
      userDao.delete(user);
   }

   @Transactional(readOnly = true)
   @Override
   public List<User> listUsers() {
      return userDao.listUsers();
   }

   @Override
   public User getUserById(Long id) {
      return userDao.getUserById(id);
   }

   @Transactional
   @Override
   public Set<Role> listRoles() {
      return roleDao.getRoles();
   }

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

   @Transactional
   @Override
   public Set<Role> getRolesByIds(List<Long> roleIds) {
      return roleDao.findRolesByIds(roleIds);
   }

   public User getCurrentUser() {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      return  (User) authentication.getPrincipal();
   }
}
