package br.edu.ifmg.locadora.resources;

import br.edu.ifmg.locadora.dtos.RentalDTO;
import br.edu.ifmg.locadora.services.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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


    @Operation(
            description ="Buscar todos alugueis",
            summary = "Buscar todos alugueis",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    @GetMapping(produces = "application/json")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<RentalDTO>> findAll(Pageable pageable) {
        Page<RentalDTO> page = rentalService.findAll(pageable);
        return ResponseEntity.ok().body(page);
    }


    @Operation(
            description ="Buscar alugueis por ID",
            summary = "Buscar alugueis por ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        })
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<RentalDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.findById(id));
    }


    @Operation(
            description ="Criar novo aluguel",
            summary = "Criar novo aluguel",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity")
            })
    @PostMapping(consumes = "application/json", produces = "application/json")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<RentalDTO> insert(@Valid @RequestBody RentalDTO dto) {
        dto = rentalService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }



    @Operation(
            description ="Atualizar aluguel pelo ID",
            summary = "Atualizar aluguel pelo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity")
            })
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<RentalDTO> update(@PathVariable Long id, @Valid @RequestBody RentalDTO dto) {
        dto = rentalService.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @Operation(
            description ="Deletar aluguel pelo ID",
            summary = "Deletar aluguel pelo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    @DeleteMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<RentalDTO> delete(@PathVariable Long id) {
        return ResponseEntity.ok().body(rentalService.delete(id));
    }

    @Operation(
            description = "Calcular o valor total dos alugueis do cliente",
            summary = "Calcular o valor total dos alugueis do cliente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Client Not Found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CLIENT')")
    @GetMapping(value = "/user/{userId}/total", produces = "application/json")
    public ResponseEntity<Map<String, BigDecimal>> getTotalValueByUser(@PathVariable Long userId) {
        BigDecimal totalValue = rentalService.getTotalValueByUser(userId);
        Map<String, BigDecimal> response = Map.of("total_alugueis", totalValue);
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            description = "Buscar aluguel com maior valor do cliente pelo ID",
            summary = "Buscar aluguel com maior valor do cliente pelo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Client Not Found or No Stays Found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CLIENT')")
    @GetMapping(value = "/user/{userId}/highest", produces = "application/json")
    public ResponseEntity<RentalDTO> getHighestValueRentalByUser(@PathVariable Long userId) {
        RentalDTO highestValueRental = rentalService.getHighestValueRentalByUser(userId);
        return ResponseEntity.ok().body(highestValueRental);
    }

    @Operation(
            description = "Buscar aluguel com menor valor do cliente pelo ID",
            summary = "Buscar aluguel com menor valor do cliente pelo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Client Not Found or No Stays Found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CLIENT')")
    @GetMapping(value = "/user/{userId}/lowest", produces = "application/json")
    public ResponseEntity<RentalDTO> getLowestValueRentalByUser(@PathVariable Long userId) {
        RentalDTO lowestValueRental = rentalService.getLowestValueRentalByUser(userId);
        return ResponseEntity.ok().body(lowestValueRental);
    }
}