package br.edu.ifmg.locadora.services;

import br.edu.ifmg.locadora.dtos.RoleDTO;
import br.edu.ifmg.locadora.dtos.UserDTO;
import br.edu.ifmg.locadora.dtos.UserInsertDTO;
import br.edu.ifmg.locadora.entities.Role;
import br.edu.ifmg.locadora.entities.User;
import br.edu.ifmg.locadora.projections.UserDetailsProjection;
import br.edu.ifmg.locadora.repositories.RoleRepository;
import br.edu.ifmg.locadora.repositories.UserRepository;
import br.edu.ifmg.locadora.resources.UserResource;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.time.Instant;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return page.map(user -> new UserDTO(user)
                .add(linkTo(methodOn(UserResource.class).findAll(null)).withSelfRel())
                .add(linkTo(methodOn(UserResource.class).findById(user.getId())).withRel("Get a user"))
        );
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado! ID: " + id)
        );
        return new UserDTO(user)
                .add(linkTo(methodOn(UserResource.class).findById(id)).withSelfRel())
                .add(linkTo(methodOn(UserResource.class).findAll(null)).withRel("Get all users"))
                .add(linkTo(methodOn(UserResource.class).update(id, new UserDTO(user))).withRel("Update a user"))
                .add(linkTo(methodOn(UserResource.class).delete(id)).withRel("Delete a user"));
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);


        entity.setPassword(passwordEncoder.encode(dto.getPhone()));
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        entity = userRepository.save(entity);
        return new UserDTO(entity)
                .add(linkTo(methodOn(UserResource.class).findById(entity.getId())).withRel("Get a user"))
                .add(linkTo(methodOn(UserResource.class).findAll(null)).withRel("Get all users"))
                .add(linkTo(methodOn(UserResource.class).update(entity.getId(), new UserDTO(entity))).withRel("Update a user"))
                .add(linkTo(methodOn(UserResource.class).delete(entity.getId())).withRel("Delete user"));
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        try {
            User entity = userRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity.setUpdatedAt(Instant.now());
            entity = userRepository.save(entity);
            return new UserDTO(entity)
                    .add(linkTo(methodOn(UserResource.class).findById(id)).withRel("Find user by id"))
                    .add(linkTo(methodOn(UserResource.class).findAll(null)).withRel("All users"))
                    .add(linkTo(methodOn(UserResource.class).delete(id)).withRel("Delete user"));
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Usuário não encontrado! ID: " + id);
        }
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado! ID: " + id);
        }
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Violação de integridade: este usuário não pode ser deletado.");
        }
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllClients(Pageable pageable) {
        Page<User> page = userRepository.findAllClients(pageable);
        return page.map(user -> new UserDTO(user)
                .add(linkTo(methodOn(UserResource.class).findAllClients(null)).withSelfRel())
                .add(linkTo(methodOn(UserResource.class).findClientById(user.getId())).withRel("Get a client"))
        );
    }


    @Transactional(readOnly = true)
    public UserDTO findClientById(Long id) {
        User user = userRepository.findClientById(id).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado ou usuário não é um cliente! ID: " + id)
        );
        return new UserDTO(user)
                .add(linkTo(methodOn(UserResource.class).findClientById(id)).withSelfRel())
                .add(linkTo(methodOn(UserResource.class).findAllClients(null)).withRel("Get all clients"));
    }

    private void copyDtoToEntity(UserDTO dto, User entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setUsername(dto.getUsername());
        for (RoleDTO roleDTO : dto.getRoles()) {
            String authority = roleDTO.getAuthority();
            Role role = roleRepository.findByAuthority(authority);
            if (role == null) {
                throw new IllegalArgumentException("Role inválida: " + authority);
            }
            entity.getRoles().add(role);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRoleByUsername(username);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }

        User user = new User();
        user.setUsername(result.get(0).getUsername());
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }

        return user;
    }



    @Transactional
    public UserDTO signUp(UserInsertDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);
        Role role = roleRepository.findByAuthority("ROLE_CLIENT");
        if (role == null) {
            throw new RuntimeException("Erro: A role 'ROLE_CLIENT' não foi encontrada.");
        }
        entity.getRoles().clear();
        entity.getRoles().add(role);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        User savedUser = userRepository.save(entity);
        return new UserDTO(savedUser);
    }
}