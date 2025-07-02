package br.edu.ifmg.locadora.services;

import br.edu.ifmg.locadora.dtos.VehicleDTO;
import br.edu.ifmg.locadora.entities.Vehicle;
import br.edu.ifmg.locadora.repositories.RentalRepository;
import br.edu.ifmg.locadora.repositories.VehicleRepository;
import br.edu.ifmg.locadora.resources.VehicleResource;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private RentalRepository rentalRepository;

    @Transactional(readOnly = true)
    public Page<VehicleDTO> findAll(Pageable pageable) {
        Page<Vehicle> page = vehicleRepository.findAll(pageable);
        return page.map(
                vehicle -> new VehicleDTO(vehicle)
                        .add(linkTo(methodOn(VehicleResource.class).findAll(null)).withSelfRel())
                        .add(linkTo(methodOn(VehicleResource.class).findById(vehicle.getId())).withRel("Get a vehicle"))
        );
    }

    @Transactional(readOnly = true)
    public VehicleDTO findById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Veículo não encontrado! ID: " + id)
        );
        return new VehicleDTO(vehicle)
                .add(linkTo(methodOn(VehicleResource.class).findById(id)).withSelfRel())
                .add(linkTo(methodOn(VehicleResource.class).update(id, new VehicleDTO(vehicle))).withRel("Update a vehicle"))
                .add(linkTo(methodOn(VehicleResource.class).delete(id)).withRel("Delete a vehicle"));
    }

    @Transactional
    public VehicleDTO insert(VehicleDTO dto) {
        Vehicle entity = new Vehicle();
        copyDtoToEntity(dto, entity);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        entity = vehicleRepository.save(entity);
        return new VehicleDTO(entity)
                .add(linkTo(methodOn(VehicleResource.class).findById(entity.getId())).withRel("Get a vehicle"))
                .add(linkTo(methodOn(VehicleResource.class).findAll(null)).withRel("All vehicles"))
                .add(linkTo(methodOn(VehicleResource.class).update(entity.getId(), new VehicleDTO(entity))).withRel("Update vehicle"))
                .add(linkTo(methodOn(VehicleResource.class).delete(entity.getId())).withRel("Delete vehicle"));
    }

    @Transactional
    public VehicleDTO update(Long id, VehicleDTO dto) {
        try {
            Vehicle entity = vehicleRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity.setUpdatedAt(Instant.now());
            entity = vehicleRepository.save(entity);
            return new VehicleDTO(entity)
                    .add(linkTo(methodOn(VehicleResource.class).findById(entity.getId())).withSelfRel())
                    .add(linkTo(methodOn(VehicleResource.class).findAll(null)).withRel("All vehicles"))
                    .add(linkTo(methodOn(VehicleResource.class).delete(entity.getId())).withRel("Delete vehicle"));
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Veículo não encontrado! ID: " + id);
        }
    }

    @Transactional
    public VehicleDTO delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new RuntimeException("Veículo não encontrado! ID: " + id);
        }

        // Verificação proativa: veja se existem aluguéis associados ANTES de deletar.
        if (rentalRepository.existsByVehicleId(id)) {
            throw new RuntimeException("Violação de integridade: este veículo não pode ser deletado, pois possui aluguéis associados.");
        }

        try {
            Vehicle vehicle = vehicleRepository.getReferenceById(id);
            vehicleRepository.delete(vehicle);
            return new VehicleDTO(vehicle);
        } catch (DataIntegrityViolationException e) {
            // Este bloco agora serve como uma segunda camada de segurança.
            throw new RuntimeException("Violação de integridade: este veículo não pode ser deletado.");
        }
    }


    @Transactional(readOnly = true)
    public List<VehicleDTO> findAvailable(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("A data de início não pode ser posterior à data de fim.");
        }

        // Converte LocalDate para Instant
        Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);

        List<Vehicle> vehicles = vehicleRepository.findAvailableVehicles(startInstant, endInstant);

        return vehicles.stream()
                .map(vehicle -> new VehicleDTO(vehicle)
                        .add(linkTo(methodOn(VehicleResource.class).findById(vehicle.getId())).withSelfRel()))
                .collect(Collectors.toList());
    }

    private void copyDtoToEntity(VehicleDTO dto, Vehicle entity) {
        entity.setBrand(dto.getBrand());
        entity.setModel(dto.getModel());
        entity.setLicensePlate(dto.getLicensePlate());
        entity.setDailyRate(dto.getDailyRate());
        entity.setImageUrl(dto.getImageUrl());
    }
}