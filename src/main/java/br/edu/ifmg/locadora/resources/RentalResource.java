package br.edu.ifmg.locadora.resources;

import br.edu.ifmg.locadora.dtos.RentalDTO;
import br.edu.ifmg.locadora.services.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping(value = "/rentals")
public class RentalResource {

    @Autowired
    private RentalService rentalService;

    @GetMapping
    public ResponseEntity<Page<RentalDTO>> findAll(Pageable pageable) {
        Page<RentalDTO> page = rentalService.findAll(pageable);
        return ResponseEntity.ok().body(page);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RentalDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RentalDTO> insert(@Valid @RequestBody RentalDTO dto) {
        dto = rentalService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }



    @PutMapping(value = "/{id}")
    public ResponseEntity<RentalDTO> update(@PathVariable Long id, @Valid @RequestBody RentalDTO dto) {
        dto = rentalService.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }


    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<RentalDTO> delete(@PathVariable Long id) {
        return ResponseEntity.ok().body(rentalService.delete(id));
    }


    @GetMapping(value = "/user/{userId}/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalValueByUser(@PathVariable Long userId) {
        BigDecimal totalValue = rentalService.getTotalValueByUser(userId);
        Map<String, BigDecimal> response = Map.of("total_alugueis", totalValue);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/user/{userId}/highest")
    public ResponseEntity<RentalDTO> getHighestValueRentalByUser(@PathVariable Long userId) {
        RentalDTO highestValueRental = rentalService.getHighestValueRentalByUser(userId);
        return ResponseEntity.ok().body(highestValueRental);
    }

    @GetMapping(value = "/user/{userId}/lowest")
    public ResponseEntity<RentalDTO> getLowestValueRentalByUser(@PathVariable Long userId) {
        RentalDTO lowestValueRental = rentalService.getLowestValueRentalByUser(userId);
        return ResponseEntity.ok().body(lowestValueRental);
    }
}