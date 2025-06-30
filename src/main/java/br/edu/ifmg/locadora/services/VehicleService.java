package br.edu.ifmg.locadora.services;

import br.edu.ifmg.locadora.dtos.VehicleDTO;
import br.edu.ifmg.locadora.entities.Vehicle;
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

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

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
        try {
            // Lógica exata do seu exemplo: usa getReferenceById
            Vehicle vehicle = vehicleRepository.getReferenceById(id);
            vehicleRepository.delete(vehicle);
            return new VehicleDTO(vehicle);

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Violação de integridade: este veículo não pode ser deletado, pois possui aluguéis associados.");
        }
    }

    private void copyDtoToEntity(VehicleDTO dto, Vehicle entity) {
        entity.setBrand(dto.getBrand());
        entity.setModel(dto.getModel());
        entity.setLicensePlate(dto.getLicensePlate());
        entity.setDailyRate(dto.getDailyRate());
        entity.setImageUrl(dto.getImageUrl());
    }
}