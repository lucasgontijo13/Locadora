package br.edu.ifmg.locadora.repositories;


import br.edu.ifmg.locadora.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query("SELECT v FROM Vehicle v WHERE v.id NOT IN (" +
            "SELECT r.vehicle.id FROM Rental r WHERE " +
            "r.rentalDate < :endDate AND r.returnDate > :startDate" +
            ")")
    List<Vehicle> findAvailableVehicles(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}