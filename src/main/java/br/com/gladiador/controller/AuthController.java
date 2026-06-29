package br.com.gladiador.controller;

import br.com.gladiador.dto.CadastroDTO;
import br.com.gladiador.dto.LoginDTO;
import br.com.gladiador.dto.LoginResponseDTO;
import br.com.gladiador.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para autenticação e cadastro
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Endpoints de autenticação e cadastro")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Realiza login no sistema", description = "Autentica usuário e retorna token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
        try {
            LoginResponseDTO response = authService.login(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/cadastro")
    @Operation(summary = "Cadastra novo aluno", description = "Registra um novo aluno no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cadastro realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou duplicados")
    })
    public ResponseEntity<?> cadastrar(@Valid @RequestBody CadastroDTO dto) {
        try {
            authService.cadastrar(dto);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Cadastro realizado com sucesso");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
