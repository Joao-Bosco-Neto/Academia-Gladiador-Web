package br.com.gladiador.service;

import br.com.gladiador.dto.AlunoDTO;
import br.com.gladiador.dto.AtualizarAlunoDTO;
import br.com.gladiador.model.Aluno;
import br.com.gladiador.model.Matricula;
import br.com.gladiador.model.Plano;
import br.com.gladiador.model.Usuario;
import br.com.gladiador.repository.AlunoRepository;
import br.com.gladiador.repository.MatriculaRepository;
import br.com.gladiador.repository.PlanoRepository;
import br.com.gladiador.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações relacionadas aos alunos
 */
@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanoRepository planoRepository;
    private final MatriculaRepository matriculaRepository;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoRepository alunoRepository, UsuarioRepository usuarioRepository,
                        PlanoRepository planoRepository, MatriculaRepository matriculaRepository,
                        PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.planoRepository = planoRepository;
        this.matriculaRepository = matriculaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Lista todos os alunos
     */
    public List<AlunoDTO> listarTodos() {
        return alunoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca alunos por nome ou CPF
     */
    public List<AlunoDTO> buscar(String termo) {
        return alunoRepository.buscarPorNomeOuCpf(termo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um aluno por ID
     */
    public AlunoDTO buscarPorId(Integer id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        return convertToDTO(aluno);
    }

    /**
     * Busca um aluno por email do usuário (para o próprio aluno consultar seus dados)
     */
    public AlunoDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Aluno aluno = alunoRepository.findAll().stream()
                .filter(a -> a.getUsuario().getId().equals(usuario.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        return convertToDTO(aluno);
    }

    /**
     * Atualiza os dados de um aluno
     */
    @Transactional
    public void atualizar(Integer id, AtualizarAlunoDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Usuario usuario = aluno.getUsuario();

        // Verificar duplicatas (exceto o próprio aluno)
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (!aluno.getCpf().equals(dto.getCpf()) && alunoRepository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        // Calcular IMC
        BigDecimal imc = dto.getPeso().divide(
                dto.getAltura().multiply(dto.getAltura()),
                2,
                RoundingMode.HALF_UP
        );

        String classificacaoImc = classificarImc(imc);

        // Atualizar usuário
        usuario.setEmail(dto.getEmail());
        // Atualizar senha somente se foi fornecida (não vazia/nula)
        if (dto.getSenha() != null && !dto.getSenha().trim().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        usuarioRepository.save(usuario);

        // Atualizar aluno
        aluno.setNome(dto.getNome());
        aluno.setSexo(converterSexo(dto.getSexo()));
        aluno.setIdade(dto.getIdade());
        aluno.setCpf(dto.getCpf());
        aluno.setPeso(dto.getPeso());
        aluno.setAltura(dto.getAltura());
        aluno.setImc(imc);
        aluno.setClassificacaoImc(classificacaoImc);
        alunoRepository.save(aluno);

        // Atualizar matrícula se o plano foi alterado
        Matricula matricula = matriculaRepository.findByAlunoId(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada"));

        if (!matricula.getPlano().getNome().equals(dto.getPlano())) {
            Plano novoPlano = planoRepository.findByNome(dto.getPlano())
                    .orElseThrow(() -> new RuntimeException("Plano não encontrado: " + dto.getPlano()));

            matricula.setPlano(novoPlano);
            LocalDate novaDataVencimento = calcularDataVencimento(matricula.getDataInicio(), dto.getPlano());
            matricula.setDataVencimento(novaDataVencimento);
            matriculaRepository.save(matricula);
        }
    }

    /**
     * Deleta um aluno (cascade deleta usuário e matrícula)
     */
    @Transactional
    public void deletar(Integer id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Usuario usuario = aluno.getUsuario();
        alunoRepository.delete(aluno);
        usuarioRepository.delete(usuario);
    }

    /**
     * Altera o status da matrícula de um aluno
     */
    @Transactional
    public void alterarStatus(Integer id, String status) {
        if (!status.equals("Ativo") && !status.equals("Inativo")) {
            throw new RuntimeException("Status inválido. Use 'Ativo' ou 'Inativo'");
        }

        Matricula matricula = matriculaRepository.findByAlunoId(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada"));

        matricula.setStatus(status);
        matriculaRepository.save(matricula);
    }

    /**
     * Converte entidade Aluno para DTO
     */
    private AlunoDTO convertToDTO(Aluno aluno) {
        AlunoDTO dto = new AlunoDTO();
        dto.setId(aluno.getId());
        dto.setNome(aluno.getNome());
        dto.setSexo(aluno.getSexo());
        dto.setIdade(aluno.getIdade());
        dto.setCpf(aluno.getCpf());
        dto.setPeso(aluno.getPeso());
        dto.setAltura(aluno.getAltura());
        dto.setImc(aluno.getImc());
        dto.setClassificacaoImc(aluno.getClassificacaoImc());
        dto.setEmail(aluno.getUsuario().getEmail());
        dto.setTipoUsuario(aluno.getUsuario().getTipoUsuario());
        dto.setDataCadastro(aluno.getDataCadastro());

        // Buscar dados da matrícula
        matriculaRepository.findByAlunoId(aluno.getId()).ifPresent(matricula -> {
            dto.setPlano(matricula.getPlano().getNome());
            dto.setMensalidade(matricula.getPlano().getValor());
            dto.setStatus(matricula.getStatus());
            dto.setDataInicio(matricula.getDataInicio());
            dto.setDataVencimento(matricula.getDataVencimento());
        });

        return dto;
    }

    /**
     * Classifica o IMC
     */
    private String classificarImc(BigDecimal imc) {
        if (imc.compareTo(new BigDecimal("18.5")) < 0) {
            return "Abaixo do peso";
        } else if (imc.compareTo(new BigDecimal("25")) < 0) {
            return "Peso normal";
        } else if (imc.compareTo(new BigDecimal("30")) < 0) {
            return "Sobrepeso";
        } else {
            return "Obesidade";
        }
    }

    /**
     * Calcula a data de vencimento baseada no plano
     */
    private LocalDate calcularDataVencimento(LocalDate dataInicio, String plano) {
        return switch (plano) {
            case "Anual" -> dataInicio.plusYears(1);
            case "Mensal" -> dataInicio.plusMonths(1);
            case "Diária" -> dataInicio.plusDays(1);
            default -> dataInicio.plusMonths(1);
        };
    }

    /**
     * Converte o código de sexo (M/F) para o formato extenso do banco (Masculino/Feminino)
     */
    private String converterSexo(String sexoCodigo) {
        return switch (sexoCodigo) {
            case "M" -> "Masculino";
            case "F" -> "Feminino";
            default -> sexoCodigo;
        };
    }
}
