package br.edu.ifmg.locadora.resources;

import br.edu.ifmg.locadora.dtos.InvoiceDTO;
import br.edu.ifmg.locadora.services.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoices")
@Tag(name = "Invoice", description = "Controller para geração de faturas")
public class InvoiceResource {

    @Autowired
    private InvoiceService invoiceService;

    @Operation(
            summary = "Gera a fatura de um usuário",
            description = "Retorna todos os aluguéis e o custo total para um usuário específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado ou sem aluguéis")
            }
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<InvoiceDTO> generateInvoice(@PathVariable Long userId) {
        InvoiceDTO invoice = invoiceService.generateInvoice(userId);
        return ResponseEntity.ok(invoice);
    }
}