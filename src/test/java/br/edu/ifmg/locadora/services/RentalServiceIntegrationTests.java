package br.edu.ifmg.locadora.services;

import br.edu.ifmg.locadora.dtos.RentalDTO;
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
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RentalServiceIntegrationTests {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private User user;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("rentaluser@example.com");
        user.setUsername("rentaluser");
        user.setPassword("password");
        userRepository.save(user);

        vehicle = new Vehicle();
        vehicle.setBrand("Test Brand");
        vehicle.setModel("Test Model");
        vehicle.setLicensePlate("TEST-1234");
        vehicle.setDailyRate(100.0);
        vehicleRepository.save(vehicle);
    }

    @Test
    public void insertShouldCreateRentalWhenDataIsValid() {
        Instant rentalDate = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant returnDate = Instant.now().plus(5, ChronoUnit.DAYS);

        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setUser(new br.edu.ifmg.locadora.dtos.UserDTO(user));
        rentalDTO.setVehicle(new br.edu.ifmg.locadora.dtos.VehicleDTO(vehicle));
        rentalDTO.setRentalDate(rentalDate);
        rentalDTO.setReturnDate(returnDate);

        RentalDTO result = rentalService.insert(rentalDTO);

        assertNotNull(result.getId());
        assertEquals(user.getId(), result.getUser().getId());
        assertEquals(vehicle.getId(), result.getVehicle().getId());
    }

    @Test
    public void insertShouldThrowExceptionWhenVehicleIsAlreadyRented() {
        Instant rentalDate = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant returnDate = Instant.now().plus(5, ChronoUnit.DAYS);

        Rental existingRental = new Rental(user, vehicle, rentalDate, returnDate);
        rentalRepository.save(existingRental);

        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setUser(new br.edu.ifmg.locadora.dtos.UserDTO(user));
        rentalDTO.setVehicle(new br.edu.ifmg.locadora.dtos.VehicleDTO(vehicle));
        rentalDTO.setRentalDate(rentalDate.plus(1, ChronoUnit.DAYS)); // Overlapping
        rentalDTO.setReturnDate(returnDate.plus(1, ChronoUnit.DAYS));

        assertThrows(RuntimeException.class, () -> {
            rentalService.insert(rentalDTO);
        });
    }

    @Test
    public void deleteShouldRemoveRental() {
        Instant rentalDate = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant returnDate = Instant.now().plus(5, ChronoUnit.DAYS);
        Rental rental = new Rental(user, vehicle, rentalDate, returnDate);
        rentalRepository.save(rental);

        rentalService.delete(rental.getId());

        assertFalse(rentalRepository.existsById(rental.getId()));
    }
}