package br.edu.ifmg.locadora.services;

import br.edu.ifmg.locadora.dtos.InvoiceDTO;
import br.edu.ifmg.locadora.dtos.RentalDTO;
import br.edu.ifmg.locadora.dtos.UserDTO;
import br.edu.ifmg.locadora.entities.Rental;
import br.edu.ifmg.locadora.entities.User;
import br.edu.ifmg.locadora.repositories.RentalRepository;
import br.edu.ifmg.locadora.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Transactional(readOnly = true)
    public InvoiceDTO generateInvoice(Long userId) {
        // 1. Busca o usuário
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário com id " + userId + " não encontrado"));

        // 2. Busca todos os aluguéis daquele usuário
        List<Rental> rentals = rentalRepository.findByUserId(userId);
        if (rentals.isEmpty()) {
            throw new RuntimeException("Não há aluguéis para o usuário com id " + userId);
        }

        // 3. Converte a lista de entidades para uma lista de DTOs
        List<RentalDTO> rentalDTOs = rentals.stream()
                .map(RentalDTO::new)
                .collect(Collectors.toList());

        // 4. Calcula o valor total somando o valor de cada aluguel
        BigDecimal total = rentals.stream()
                .map(Rental::getTotalValue) // Usa o método que criamos na entidade Rental!
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Cria e retorna o DTO da fatura
        return new InvoiceDTO(new UserDTO(user), rentalDTOs, total);
    }
}