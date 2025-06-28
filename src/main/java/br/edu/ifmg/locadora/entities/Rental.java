package br.edu.ifmg.locadora.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "tb_rental")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Relacionamento com o cliente que alugou
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // Relacionamento com o veículo alugado
    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    // Data/hora de retirada do veículo
    @Column(name = "rental_date", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
    private Instant rentalDate;

    // Data/hora de devolução do veículo
    @Column(name = "return_date", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
    private Instant returnDate;

    // Construtor para facilitar a criação
    public Rental(User user, Vehicle vehicle, Instant rentalDate, Instant returnDate) {
        this.user = user;
        this.vehicle = vehicle;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
    }


    public BigDecimal getTotalValue() {
        if (vehicle == null || rentalDate == null || returnDate == null) {
            return BigDecimal.ZERO;
        }

        // 1. Calcula a duração exata entre as duas datas
        Duration duration = Duration.between(rentalDate, returnDate);

        // 2. Converte a duração para dias, arredondando para cima.
        // O truque é dividir o total de segundos da duração pelo número de segundos em um dia (86400)
        // e usar Math.ceil() para garantir que qualquer fração de dia conte como um dia inteiro.
        long days = (long) Math.ceil((double) duration.getSeconds() / (24 * 60 * 60));

        // 3. Garante que o mínimo seja 1 dia, mesmo que o aluguel seja por poucas horas.
        if (days == 0) {
            days = 1;
        }

        BigDecimal dailyRate = BigDecimal.valueOf(vehicle.getDailyRate());
        return dailyRate.multiply(BigDecimal.valueOf(days));
    }
}