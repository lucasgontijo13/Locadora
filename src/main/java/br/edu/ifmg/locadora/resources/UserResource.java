package br.edu.ifmg.locadora.resources;

import br.edu.ifmg.locadora.dtos.UserDTO;
import br.edu.ifmg.locadora.dtos.UserInsertDTO;
import br.edu.ifmg.locadora.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de Usuários e Clientes")
public class UserResource {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Busca todos os usuários",
            description = "Retorna uma lista paginada de todos os usuários do sistema, incluindo clientes e administradores. Acesso restrito a administradores.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado")
            }
    )
    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
        Page<UserDTO> page = userService.findAll(pageable);
        return ResponseEntity.ok().body(page);
    }

    @Operation(
            summary = "Busca um usuário por ID",
            description = "Retorna os detalhes de um usuário específico. Acesso permitido para administradores ou para o próprio usuário.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            }
    )
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO dto = userService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @Operation(
            summary = "Insere um novo usuário",
            description = "Cria um novo usuário com perfil a escolha. Acesso restrito a administradores.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "422", description = "Entidade não processável (ex: e-mail ou username já existe)")
            }
    )
    @PostMapping(produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO dto) {
        UserDTO newDto = userService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newDto.getId()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @Operation(
            summary = "Atualiza um usuário",
            description = "Atualiza os dados de um usuário existente. Acesso restrito a administradores.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "422", description = "Entidade não processável")
            }
    )
    @PutMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        dto = userService.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @Operation(
            summary = "Deleta um usuário",
            description = "Remove um usuário do sistema. Retorna o objeto deletado. Acesso restrito a administradores.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida (ex: violação de integridade)"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            }
    )
    @DeleteMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Busca todos os clientes (Admin)",
            description = "Retorna uma lista paginada de todos os usuários com o perfil 'ROLE_CLIENT'. Acesso restrito a administradores.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado")
            }
    )
    @GetMapping(value = "/clients", produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<UserDTO>> findAllClients(Pageable pageable) { // O tipo aqui deve ser o do Spring
        Page<UserDTO> page = userService.findAllClients(pageable);
        return ResponseEntity.ok().body(page);
    }


    @Operation(
            summary = "Busca um cliente por ID",
            description = "Retorna os detalhes de um usuário-cliente. Acesso permitido para administradores ou para o próprio cliente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            }
    )
    @GetMapping(value = "/clients/{id}", produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> findClientById(@PathVariable Long id) {
        UserDTO dto = userService.findClientById(id);
        return ResponseEntity.ok().body(dto);
    }

    @Operation(
            summary = "Registra um novo cliente (público)",
            description = "Endpoint público para que novos usuários possam se registrar com o perfil 'ROLE_CLIENT'.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos")
            }
    )
    @PostMapping(value = "/signup", produces = "application/json")
    public ResponseEntity<UserDTO> signUp(@Valid @RequestBody UserInsertDTO dto) {
        UserDTO newUser = userService.signUp(dto);

        // Cria a URI para o novo recurso criado
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newUser.getId()).toUri();

        // Retorna o status 201 Created com a URI e o objeto criado no corpo
        return ResponseEntity.created(uri).body(newUser);
    }
}