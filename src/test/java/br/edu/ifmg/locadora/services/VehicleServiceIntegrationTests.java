package br.edu.ifmg.locadora.services;

import br.edu.ifmg.locadora.dtos.VehicleDTO;
import br.edu.ifmg.locadora.entities.Rental;
import br.edu.ifmg.locadora.entities.User;
import br.edu.ifmg.locadora.entities.Vehicle;
import br.edu.ifmg.locadora.repositories.RentalRepository;
import br.edu.ifmg.locadora.repositories.UserRepository;
import br.edu.ifmg.locadora.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class VehicleServiceIntegrationTests {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    private Vehicle vehicle1;
    private Vehicle vehicle2;

    @BeforeEach
    void setUp() {
        // Limpa os repositórios para garantir que os testes sejam independentes
        rentalRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();


        // Cria os veículos para os testes
        vehicle1 = new Vehicle(0L, "Brand A", "Model A", "PLATE-A", 150.0, null, Instant.now(), Instant.now());
        vehicle2 = new Vehicle(0L, "Brand B", "Model B", "PLATE-B", 200.0, null, Instant.now(), Instant.now());
        vehicleRepository.save(vehicle1);
        vehicleRepository.save(vehicle2);
    }

    private User createAndSaveTestUser(String email, String username) {
        User user = new User();
        // Define todos os campos obrigatórios (não nulos)
        user.setName("Test User");
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword("password"); // A senha será criptografada pelo serviço se necessário
        user.setPhone("1234567890");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    @Test
    public void findAvailableShouldReturnOnlyAvailableVehicles() {
        // Cria um usuário de teste preenchendo os campos obrigatórios
        User user = createAndSaveTestUser("testvehicle@example.com", "testvehicle");

        LocalDate startDate = LocalDate.now().plusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(8);

        // Aluga o vehicle1 durante o período
        Instant rentalStart = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant rentalEnd = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Rental rental = new Rental(user, vehicle1, rentalStart, rentalEnd);
        rentalRepository.save(rental);

        List<VehicleDTO> availableVehicles = vehicleService.findAvailable(startDate, endDate);

        // Apenas o vehicle2 deve estar disponível
        assertEquals(1, availableVehicles.size());
        assertEquals(vehicle2.getId(), availableVehicles.get(0).getId());
    }

    @Test
    public void findAvailableShouldReturnAllVehiclesWhenNoneAreRented() {
        LocalDate startDate = LocalDate.now().plusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(8);

        List<VehicleDTO> availableVehicles = vehicleService.findAvailable(startDate, endDate);

        // Agora o teste espera 2, pois limpamos o repositório antes
        assertEquals(2, availableVehicles.size());
    }

    @Test
    public void deleteShouldThrowExceptionWhenVehicleHasRentals() {
        // Cria um usuário de teste preenchendo os campos obrigatórios
        User user = createAndSaveTestUser("testvehicledel@example.com", "testvehicledel");

        Rental rental = new Rental(user, vehicle1, Instant.now(), Instant.now().plus(5, ChronoUnit.DAYS));
        rentalRepository.save(rental);

        // A exceção esperada é RuntimeException, pois a violação de integridade é encapsulada
        assertThrows(RuntimeException.class, () -> {
            vehicleService.delete(vehicle1.getId());
        });
    }
}