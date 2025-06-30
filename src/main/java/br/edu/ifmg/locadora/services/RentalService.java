package br.edu.ifmg.locadora.services;

import br.edu.ifmg.locadora.dtos.RentalDTO;
import br.edu.ifmg.locadora.entities.Rental;
import br.edu.ifmg.locadora.entities.User;
import br.edu.ifmg.locadora.entities.Vehicle;
import br.edu.ifmg.locadora.repositories.RentalRepository;
import br.edu.ifmg.locadora.repositories.UserRepository;
import br.edu.ifmg.locadora.repositories.VehicleRepository;
import br.edu.ifmg.locadora.resources.RentalResource;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.math.BigDecimal;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public Page<RentalDTO> findAll(Pageable pageable) {
        Page<Rental> page = rentalRepository.findAll(pageable);

        return page.map(rental -> new RentalDTO(rental)
                .add(linkTo(methodOn(RentalResource.class).findById(rental.getId())).withSelfRel())
                .add(linkTo(methodOn(RentalResource.class).findById(rental.getId())).withRel("Get rental"))
        );
    }

    @Transactional(readOnly = true)
    public RentalDTO findById(Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Aluguel não encontrado! ID: " + id)
        );
        return new RentalDTO(rental)
                .add(linkTo(methodOn(RentalResource.class).findById(id)).withSelfRel())
                .add(linkTo(methodOn(RentalResource.class).findAll(null)).withRel("Get all rentals"));
    }

    @Transactional
    public RentalDTO insert(RentalDTO dto) {
        User user = userRepository.findById(dto.getUser().getId()).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado!")
        );
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicle().getId()).orElseThrow(
                () -> new RuntimeException("Veículo não encontrado!")
        );

        if (rentalRepository.existsRentalConflict(vehicle, dto.getRentalDate(), dto.getReturnDate())) {
            throw new RuntimeException("Veículo já está alugado neste período.");
        }
        if (dto.getRentalDate().isAfter(dto.getReturnDate())) {
            throw new RuntimeException("A data de retorno não pode ser anterior à data de locação.");
        }


        Rental entity = new Rental();
        entity.setUser(user);
        entity.setVehicle(vehicle);
        entity.setRentalDate(dto.getRentalDate());
        entity.setReturnDate(dto.getReturnDate());

        Rental savedEntity = rentalRepository.save(entity);
        return new RentalDTO(savedEntity)
                .add(linkTo(methodOn(RentalResource.class).findById(savedEntity.getId())).withRel("Get rental"))
                .add(linkTo(methodOn(RentalResource.class).findAll(null)).withRel("Get all rentals"));
    }

    @Transactional
    public RentalDTO update(Long id, RentalDTO dto) {
        try {
            Rental entity = rentalRepository.getReferenceById(id);

            // Carrega as entidades relacionadas para garantir que existam
            User user = userRepository.findById(dto.getUser().getId()).orElseThrow(
                    () -> new RuntimeException("Usuário não encontrado!")
            );
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicle().getId()).orElseThrow(
                    () -> new RuntimeException("Veículo não encontrado!")
            );

            if (dto.getRentalDate().isAfter(dto.getReturnDate())) {
                throw new RuntimeException("A data de retorno não pode ser anterior à data de locação.");
            }

            if (rentalRepository.existsRentalConflict(vehicle, dto.getRentalDate(), dto.getReturnDate())) {

                throw new RuntimeException("Período de aluguel indisponível.");
            }

            entity.setUser(user);
            entity.setVehicle(vehicle);
            entity.setRentalDate(dto.getRentalDate());
            entity.setReturnDate(dto.getReturnDate());

            entity = rentalRepository.save(entity);
            return new RentalDTO(entity)
                    .add(linkTo(methodOn(RentalResource.class).findById(id)).withSelfRel())
                    .add(linkTo(methodOn(RentalResource.class).findAll(null)).withRel("Get all rentals"));
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Aluguel não encontrado! ID: " + id);
        }
    }

    @Transactional
    public RentalDTO delete(Long id) {
        if (!rentalRepository.existsById(id)) {
            throw new RuntimeException("Aluguel não encontrado! ID: " + id);
        }
        try {
            Rental rental = rentalRepository.getReferenceById(id);
            rentalRepository.delete(rental);
            return new RentalDTO(rental);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Violação de integridade ao deletar aluguel.");
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalValueByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuário não encontrado! ID: " + userId);
        }
        BigDecimal totalValue = rentalRepository.findTotalValueByUser(userId);
        return totalValue != null ? totalValue : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public RentalDTO getHighestValueRentalByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuário não encontrado! ID: " + userId);
        }
        Rental highestValueRental = rentalRepository.findHighestValueRentalByUser(userId)
                .orElseThrow(() -> new RuntimeException("Nenhum aluguel encontrado para o usuário ID: " + userId));

        return new RentalDTO(highestValueRental)
                .add(linkTo(methodOn(RentalResource.class).findById(highestValueRental.getId())).withSelfRel());

    }

    @Transactional(readOnly = true)
    public RentalDTO getLowestValueRentalByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuário não encontrado! ID: " + userId);
        }
        Rental lowestValueRental = rentalRepository.findLowestValueRentalByUser(userId)
                .orElseThrow(() -> new RuntimeException("Nenhum aluguel encontrado para o usuário ID: " + userId));

        return new RentalDTO(lowestValueRental)
                .add(linkTo(methodOn(RentalResource.class).findById(lowestValueRental.getId())).withSelfRel());
    }
}