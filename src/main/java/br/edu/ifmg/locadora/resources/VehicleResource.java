package br.edu.ifmg.locadora.resources;

import br.edu.ifmg.locadora.dtos.VehicleDTO;
import br.edu.ifmg.locadora.services.VehicleService;
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
@RequestMapping(value = "/vehicles")
public class VehicleResource {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<Page<VehicleDTO>> findAll(Pageable pageable) {
        Page<VehicleDTO> page = vehicleService.findAll(pageable);
        return ResponseEntity.ok().body(page);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<VehicleDTO> findById(@PathVariable Long id) {
        VehicleDTO dto = vehicleService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<VehicleDTO> insert(@Valid @RequestBody VehicleDTO dto) {
        dto = vehicleService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<VehicleDTO> update(@PathVariable Long id, @Valid @RequestBody VehicleDTO dto) {
        dto = vehicleService.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<VehicleDTO> delete(@PathVariable Long id) {
        return ResponseEntity.ok().body(vehicleService.delete(id));
    }
}