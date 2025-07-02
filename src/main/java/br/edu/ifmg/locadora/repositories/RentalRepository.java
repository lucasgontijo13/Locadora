package br.edu.ifmg.locadora.repositories; // ou .repository

import br.edu.ifmg.locadora.entities.Rental;
import br.edu.ifmg.locadora.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Rental r " +
            "WHERE r.vehicle = :vehicle " +
            "AND r.rentalDate < :returnDate " +
            "AND r.returnDate > :rentalDate")
    boolean existsRentalConflict(Vehicle vehicle, Instant rentalDate, Instant returnDate);


    List<Rental> findByUserId(Long userId);

    @Query(nativeQuery = true, value = """
        SELECT SUM(v.daily_rate * DATEDIFF('DAY', r.rental_date, r.return_date))
        FROM tb_rental r
        JOIN tb_vehicle v ON r.vehicle_id = v.id
        WHERE r.user_id = :userId
    """)
    BigDecimal findTotalValueByUser(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = """
        SELECT r.*
        FROM tb_rental r
        JOIN tb_vehicle v ON r.vehicle_id = v.id
        WHERE r.user_id = :userId
        ORDER BY (v.daily_rate * DATEDIFF('DAY', r.rental_date, r.return_date)) DESC
        LIMIT 1
    """)
    Optional<Rental> findHighestValueRentalByUser(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = """
        SELECT r.*
        FROM tb_rental r
        JOIN tb_vehicle v ON r.vehicle_id = v.id
        WHERE r.user_id = :userId
        ORDER BY (v.daily_rate * DATEDIFF('DAY', r.rental_date, r.return_date)) ASC
        LIMIT 1
    """)
    Optional<Rental> findLowestValueRentalByUser(@Param("userId") Long userId);

    boolean existsByVehicleId(Long vehicleId);

}