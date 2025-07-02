package br.edu.ifmg.locadora.services;

import br.edu.ifmg.locadora.dtos.NewPasswordDTO;
import br.edu.ifmg.locadora.dtos.RequestTokenDTO;
import br.edu.ifmg.locadora.entities.PasswordRecover;
import br.edu.ifmg.locadora.entities.User;
import br.edu.ifmg.locadora.repositories.PasswordRecoverRepository;
import br.edu.ifmg.locadora.repositories.UserRepository;
import br.edu.ifmg.locadora.services.exceptions.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@Transactional
public class AuthServiceIntegrationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);
    }

    @Test
    public void createRecoverTokenShouldCreateTokenWhenEmailExists() {
        RequestTokenDTO requestTokenDTO = new RequestTokenDTO(user.getEmail());
        doNothing().when(emailService).sendEmail(any());

        authService.createRecoverToken(requestTokenDTO);

        PasswordRecover passwordRecover = passwordRecoverRepository.findAll().get(0);
        assertNotNull(passwordRecover);
        assertEquals(user.getEmail(), passwordRecover.getEmail());
    }

    @Test
    public void createRecoverTokenShouldThrowResourceNotFoundWhenEmailDoesNotExist() {
        RequestTokenDTO requestTokenDTO = new RequestTokenDTO("nonexistent@example.com");

        assertThrows(ResourceNotFound.class, () -> {
            authService.createRecoverToken(requestTokenDTO);
        });
    }

    @Test
    public void saveNewPasswordShouldUpdatePasswordWhenTokenIsValid() {
        PasswordRecover passwordRecover = new PasswordRecover(null, UUID.randomUUID().toString(), user.getEmail(), Instant.now().plusSeconds(3600));
        passwordRecoverRepository.save(passwordRecover);

        NewPasswordDTO newPasswordDTO = new NewPasswordDTO();
        newPasswordDTO.setToken(passwordRecover.getToken());
        newPasswordDTO.setNewPassword("newPassword");

        authService.saveNewPassword(newPasswordDTO);

        User updatedUser = userRepository.findByEmail(user.getEmail());
        assertTrue(passwordEncoder.matches("newPassword", updatedUser.getPassword()));
    }

    @Test
    public void saveNewPasswordShouldThrowResourceNotFoundWhenTokenIsInvalid() {
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO();
        newPasswordDTO.setToken("invalid-token");
        newPasswordDTO.setNewPassword("newPassword");

        assertThrows(ResourceNotFound.class, () -> {
            authService.saveNewPassword(newPasswordDTO);
        });
    }
}