package br.edu.ifmg.locadora.resources;

import br.edu.ifmg.locadora.dtos.RentalDTO;
import br.edu.ifmg.locadora.services.RentalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
        RentalDTO dto = rentalService.findById(id);
        return ResponseEntity.ok().body(dto);
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
        return ResponseEntity.ok(rentalService.delete(id));
    }
}