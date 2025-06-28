package br.edu.ifmg.locadora.repositories; // ou .repository

import br.edu.ifmg.locadora.entities.Rental;
import br.edu.ifmg.locadora.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Rental r " +
            "WHERE r.vehicle = :vehicle " +
            "AND r.rentalDate < :returnDate " +
            "AND r.returnDate > :rentalDate")
    boolean existsRentalConflict(Vehicle vehicle, Instant rentalDate, Instant returnDate);

    // Correção do nome do método:
    List<Rental> findByUserId(Long userId); // <<< CORREÇÃO AQUI
}