package web.dao;

import jakarta.persistence.NoResultException;
import web.model.User;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImp implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(User user) {
        entityManager.persist(user);
    }

    @Override
    public List<User> listUsers() {
        TypedQuery<User> query = entityManager.createQuery("from User", User.class);
        return query.getResultList();
    }

    @Override
    public User update(User user) {
        return entityManager.merge(user);
    }

    @Override
    public void delete(User user) {
        if (entityManager.contains(user)) {
            entityManager.remove(user);
        } else {
            entityManager.remove(entityManager.merge(user));
        }
    }

    @Override
    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery(
                "select u from User u left join fetch u.roles where u.username = :username", User.class);
        query.setParameter("username", username);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void initDatabase(List<User> users) {
        users.forEach(user -> {
            boolean userExists = entityManager.createQuery(
                            "select count(u) > 0 from User u where u.username = :username", Boolean.class)
                    .setParameter("username", user.getUsername())
                    .getSingleResult();
            if (!userExists) {
                user.getRoles().forEach(role -> {
                    boolean roleExists = entityManager.createQuery(
                                    "select count(r) > 0 from Role r where r.authority = :authority",
                                    Boolean.class)
                            .setParameter("authority", role.getAuthority())
                            .getSingleResult();
                    if (!roleExists) {
                        entityManager.persist(role);
                    }
                });
                entityManager.persist(user);
            }
        });
        entityManager.flush();
    }
}

