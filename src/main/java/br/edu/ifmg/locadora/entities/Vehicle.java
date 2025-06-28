package br.edu.ifmg.locadora.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name ="tb_vehicle")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String brand; // Marca (ex: Fiat, Chevrolet)

    @Column(nullable = false)
    private String model; // Modelo (ex: Mobi, Onix)

    @Column(nullable = false, unique = true)
    private String licensePlate; // Placa

    @Column(nullable = false)
    private double dailyRate; // Valor da Diária

    private String imageUrl;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updatedAt;
}