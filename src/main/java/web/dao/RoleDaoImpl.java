package web.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import web.model.Role;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class RoleDaoImpl implements RoleDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Set<Role> getRoles() {
        TypedQuery<Role> query = entityManager.createQuery("from Role", Role.class);
        List<Role> roles = query.getResultList();
        return new HashSet<>(roles);
    }

    @Override
    public Optional<Role> findByAuthority(String authority) {
        TypedQuery<Role> query = entityManager.createQuery("from Role as r where r.authority = :authority", Role.class);
        query.setParameter("authority", authority);

        return query.getResultList().stream().findFirst();
    }

    @Override
    public Role save(Role role) {
        return entityManager.merge(role);
    }

    @Override
    public Set<Role> findRolesByIds(List<Long> roleIds) {
        return new HashSet<>(entityManager.createQuery("SELECT r FROM Role r WHERE r.id IN :ids", Role.class)
                .setParameter("ids", roleIds)
                .getResultList());
    }
}
