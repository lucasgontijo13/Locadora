package br.edu.ifmg.locadora.repositories;


import br.edu.ifmg.locadora.entities.User;
import br.edu.ifmg.locadora.projections.UserDetailsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Query(nativeQuery = true, value = """
        SELECT u.username as username,
               u.password,
               r.id as roleId,
               r.authority
        FROM tb_user u -- ATUALIZADO
        INNER JOIN user_role ur ON u.id = ur.user_id
        INNER JOIN tb_role r ON r.id = ur.role_id -- ATUALIZADO
        WHERE u.username = :username
    """)
    List<UserDetailsProjection> searchUserAndRoleByUsername(String username);

    @Query("SELECT obj FROM User obj JOIN obj.roles r WHERE r.authority = 'ROLE_CLIENT'")
    Page<User> findAllClients(Pageable pageable);

    @Query("SELECT obj FROM User obj JOIN obj.roles r WHERE obj.id = :id AND r.authority = 'ROLE_CLIENT'")
    Optional<User> findClientById(@Param("id") Long id);
}