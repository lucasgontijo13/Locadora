package br.edu.ifmg.locadora.services;

import br.edu.ifmg.locadora.dtos.UserDTO;
import br.edu.ifmg.locadora.dtos.UserInsertDTO;
import br.edu.ifmg.locadora.entities.Role;
import br.edu.ifmg.locadora.entities.User;
import br.edu.ifmg.locadora.repositories.RoleRepository;
import br.edu.ifmg.locadora.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        // Limpa os repositórios para garantir a independência dos testes
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Agora, cria as roles necessárias para os testes em um ambiente limpo
        Role clientRole = new Role(null, "ROLE_CLIENT");
        roleRepository.save(clientRole);
    }

    @Test
    public void signUpShouldCreateNewClientUser() {
        UserInsertDTO userInsertDTO = new UserInsertDTO();
        userInsertDTO.setName("New Client");
        userInsertDTO.setEmail("newclient@example.com");
        userInsertDTO.setUsername("newclient");
        userInsertDTO.setPassword("password");
        userInsertDTO.setPhone("123456789");

        UserDTO result = userService.signUp(userInsertDTO);

        assertNotNull(result.getId());
        User user = userRepository.findById(result.getId()).get();
        assertEquals("New Client", user.getName());
        assertTrue(user.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ROLE_CLIENT")));
    }

    @Test
    public void findByIdShouldReturnUserWhenIdExists() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("findme@example.com");
        user.setUsername("findme");
        user.setPassword("password");
        // Adiciona os campos de data para evitar erros de integridade
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        UserDTO result = userService.findById(user.getId());

        assertEquals(user.getName(), result.getName());
    }

    @Test
    public void findByIdShouldThrowExceptionWhenIdDoesNotExist() {
        assertThrows(RuntimeException.class, () -> {
            // Usa um ID que certamente não existirá
            userService.findById(99999L);
        });
    }
}