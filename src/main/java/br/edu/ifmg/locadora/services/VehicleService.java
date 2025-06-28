package br.edu.ifmg.locadora.services;

import br.edu.ifmg.locadora.dtos.VehicleDTO;
import br.edu.ifmg.locadora.entities.Vehicle;
import br.edu.ifmg.locadora.repositories.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public Page<VehicleDTO> findAll(Pageable pageable) {
        Page<Vehicle> page = vehicleRepository.findAll(pageable);
        return page.map(VehicleDTO::new);
    }

    @Transactional(readOnly = true)
    public VehicleDTO findById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Veículo não encontrado! ID: " + id)
        );
        return new VehicleDTO(vehicle);
    }

    @Transactional
    public VehicleDTO insert(VehicleDTO dto) {
        Vehicle entity = new Vehicle();
        copyDtoToEntity(dto, entity);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        entity = vehicleRepository.save(entity);
        return new VehicleDTO(entity);
    }

    @Transactional
    public VehicleDTO update(Long id, VehicleDTO dto) {
        try {
            Vehicle entity = vehicleRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity.setUpdatedAt(Instant.now());
            entity = vehicleRepository.save(entity);
            return new VehicleDTO(entity);
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