package br.edu.ifmg.locadora.repositories;

import br.edu.ifmg.locadora.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByAuthority(String authority);
}