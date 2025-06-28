package br.edu.ifmg.locadora.dtos;

import br.edu.ifmg.locadora.entities.Rental;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class RentalDTO extends RepresentationModel<RentalDTO> {
    private Long id;
    @Future(message = "A data da locação deve ser uma data futura.")
    private Instant rentalDate;
    @Future(message = "A data da locação deve ser uma data futura.")
    private Instant returnDate;
    private BigDecimal totalValue;
    private UserDTO user;
    private VehicleDTO vehicle;


    public RentalDTO(Rental entity) {
        this.id = entity.getId();
        this.rentalDate = entity.getRentalDate();
        this.returnDate = entity.getReturnDate();
        this.user = new UserDTO(entity.getUser());
        this.vehicle = new VehicleDTO(entity.getVehicle());
        this.totalValue = entity.getTotalValue();
    }
}