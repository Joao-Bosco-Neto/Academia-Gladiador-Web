package br.com.gladiador.controller;

import br.com.gladiador.config.JwtUtil;
import br.com.gladiador.dto.AlunoDTO;
import br.com.gladiador.dto.CadastroDTO;
import br.com.gladiador.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller para operações com alunos (apenas admin)
 */
@RestController
@RequestMapping("/alunos")
@Tag(name = "Alunos", description = "Endpoints de gerenciamento de alunos (apenas admin)")
@SecurityRequirement(name = "bearer-jwt")
public class AlunoController {

    private final AlunoService alunoService;
    private final JwtUtil jwtUtil;

    public AlunoController(AlunoService alunoService, JwtUtil jwtUtil) {
        this.alunoService = alunoService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    @Operation(summary = "Lista todos os alunos", description = "Retorna lista completa de alunos cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<AlunoDTO>> listarTodos() {
        return ResponseEntity.ok(alunoService.listarTodos());
    }

    @GetMapping("/me")
    @Operation(summary = "Busca dados do próprio aluno", description = "Retorna os dados do aluno autenticado (liberado para qualquer usuário autenticado)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno encontrado"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<?> buscarDadosProprioAluno(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            String email = jwtUtil.extractEmail(token);
            AlunoDTO aluno = alunoService.buscarPorEmail(email);
            return ResponseEntity.ok(aluno);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca aluno por ID", description = "Retorna os dados de um aluno específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno encontrado"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        try {
            AlunoDTO aluno = alunoService.buscarPorId(id);
            return ResponseEntity.ok(aluno);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busca alunos por nome ou CPF", description = "Pesquisa alunos pelo nome ou CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<AlunoDTO>> buscar(@RequestParam String termo) {
        return ResponseEntity.ok(alunoService.buscar(termo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados de um aluno", description = "Atualiza as informações cadastrais de um aluno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @Valid @RequestBody CadastroDTO dto) {
        try {
            alunoService.atualizar(id, dto);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Aluno atualizado com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um aluno", description = "Remove um aluno do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<?> deletar(@PathVariable Integer id) {
        try {
            alunoService.deletar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Aluno deletado com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Altera status da matrícula", description = "Ativa ou inativa a matrícula de um aluno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<?> alterarStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            alunoService.alterarStatus(id, status);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Status alterado com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
