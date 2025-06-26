package br.edu.ifmg.locadora.repositories;

import br.edu.ifmg.locadora.entities.User;
import br.edu.ifmg.locadora.projections.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Query(nativeQuery = true, value = """
        SELECT u.username as username,
               u.password,
               r.id as roleId,
               r.authority
        FROM "user" u
        INNER JOIN user_role ur ON u.id = ur.user_id
        INNER JOIN role r ON r.id = ur.role_id
        WHERE u.username = :username
    """)
    List<UserDetailsProjection> searchUserAndRoleByUsername(String username);
}