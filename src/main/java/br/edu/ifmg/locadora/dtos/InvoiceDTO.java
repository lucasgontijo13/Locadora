package br.edu.ifmg.locadora.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO extends RepresentationModel<InvoiceDTO> {
    private UserDTO user;
    private List<RentalDTO> rentals;
    private BigDecimal total; // Usando BigDecimal para maior precisão monetária
}