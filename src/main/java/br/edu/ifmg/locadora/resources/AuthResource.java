package br.edu.ifmg.locadora.resources;

import br.edu.ifmg.locadora.dtos.NewPasswordDTO;
import br.edu.ifmg.locadora.dtos.RequestTokenDTO;
import br.edu.ifmg.locadora.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthResource {
    @Autowired
    private AuthService authService;

    @Operation(
            summary = "Gerar token de recuperação de senha",
            description = "Envia um e-mail (ou outro meio) ao usuário contendo um token para recuperação de senha.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Token gerado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida (dados faltando ou mal formatados)"),
                    @ApiResponse(responseCode = "422", description = "E-mail não encontrado ou já existe um token válido")
            }
    )
    @PostMapping(value = "/recover-token", consumes = "application/json")
    public ResponseEntity<Void> createRecoverToken (@Valid @RequestBody RequestTokenDTO dto) {
        authService.createRecoverToken(dto);
        return ResponseEntity.noContent().build();
    }
    @Operation(
            summary = "Redefinir senha usando token",
            description = "Recebe um token de recuperação válido e a nova senha para atualização da credencial do usuário.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida (dados faltando ou mal formatados)"),
                    @ApiResponse(responseCode = "422", description = "Token inválido ou já utilizado")
            }
    )
    @PostMapping(value = "/new-password", consumes = "application/json")
    public ResponseEntity<Void> saveNewPassword (@Valid @RequestBody NewPasswordDTO dto) {
        authService.saveNewPassword(dto);
        return ResponseEntity.noContent().build();
    }
}