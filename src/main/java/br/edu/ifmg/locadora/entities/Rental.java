package br.edu.ifmg.locadora.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

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
}