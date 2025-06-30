package br.edu.ifmg.locadora.dtos;

import br.edu.ifmg.locadora.entities.Vehicle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@NoArgsConstructor
public class VehicleDTO extends RepresentationModel<VehicleDTO> {
    private Long id;
    private String brand;
    private String model;
    private String licensePlate;
    private double dailyRate;
    private String imageUrl;

    public VehicleDTO(Vehicle entity) {
        this.id = entity.getId();
        this.brand = entity.getBrand();
        this.model = entity.getModel();
        this.licensePlate = entity.getLicensePlate();
        this.dailyRate = entity.getDailyRate();
        this.imageUrl = entity.getImageUrl();
    }
}