package br.edu.ifmg.locadora.resources;

import br.edu.ifmg.locadora.dtos.VehicleDTO;
import br.edu.ifmg.locadora.services.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/vehicles")
@Tag(name = "Veículos", description = "Endpoints para gerenciamento de veículos")
public class VehicleResource {

    @Autowired
    private VehicleService vehicleService;

    @Operation(
            summary = "Busca todos os veículos (paginado)",
            description = "Retorna uma lista paginada de todos os veículos disponíveis no sistema. Endpoint público.",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200")
            }
    )
    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<VehicleDTO>> findAll(Pageable pageable) {
        Page<VehicleDTO> page = vehicleService.findAll(pageable);
        return ResponseEntity.ok().body(page);
    }

    @Operation(
            summary = "Busca um veículo por ID",
            description = "Retorna os detalhes de um veículo específico. Endpoint público.",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            }
    )
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<VehicleDTO> findById(@PathVariable Long id) {
        VehicleDTO dto = vehicleService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @Operation(
            summary = "Insere um novo veículo ",
            description = "Cria o registro de um novo veículo no sistema. Acesso restrito a administradores.",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
    @PostMapping(produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<VehicleDTO> insert(@Valid @RequestBody VehicleDTO dto) {
        dto = vehicleService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(
            summary = "Atualiza um veículo ",
            description = "Atualiza os dados de um veículo existente. Acesso restrito a administradores.",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
    @PutMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<VehicleDTO> update(@PathVariable Long id, @Valid @RequestBody VehicleDTO dto) {
        dto = vehicleService.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @Operation(
            summary = "Deleta um veículo (Admin)",
            description = "Remove o registro de um veículo. Não pode ser executado se o veículo tiver aluguéis associados. Acesso restrito a administradores.",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            }
    )
    @DeleteMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<VehicleDTO> delete(@PathVariable Long id) {
        return ResponseEntity.ok().body(vehicleService.delete(id));
    }

    @Operation(
            summary = "Busca veículos disponíveis por período",
            description = "Retorna uma lista de todos os veículos que não possuem aluguéis conflitantes com o período de datas informado. As datas devem ser informadas no formato ISO-8601 (ex: 2024-12-25T14:00:00Z).",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request - Datas inválidas", responseCode = "400")
            }
    )
    @GetMapping(value = "/available/{startDate}/{endDate}", produces = "application/json")
    public ResponseEntity<List<VehicleDTO>> findAvailable(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<VehicleDTO> list = vehicleService.findAvailable(startDate, endDate);
        return ResponseEntity.ok().body(list);
    }
}