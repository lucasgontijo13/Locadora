package br.edu.ifmg.locadora.dtos;

import br.edu.ifmg.locadora.entities.Rental;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class RentalDTO {
    private Long id;
    private Instant rentalDate;
    private Instant returnDate;
    private UserDTO user;
    private VehicleDTO vehicle;

    public RentalDTO(Rental entity) {
        this.id = entity.getId();
        this.rentalDate = entity.getRentalDate();
        this.returnDate = entity.getReturnDate();
        this.user = new UserDTO(entity.getUser());
        this.vehicle = new VehicleDTO(entity.getVehicle());
    }
}