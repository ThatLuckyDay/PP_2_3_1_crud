package web.dao;

import web.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleDao {
    Set<Role> getRoles();

    Optional<Role> findByAuthority(String authority);

    Role save(Role role);

    Set<Role> findRolesByIds(List<Long> roleIds);
}
