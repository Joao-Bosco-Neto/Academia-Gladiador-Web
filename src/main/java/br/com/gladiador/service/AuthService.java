package br.com.gladiador.service;

import br.com.gladiador.config.JwtUtil;
import br.com.gladiador.dto.CadastroDTO;
import br.com.gladiador.dto.LoginDTO;
import br.com.gladiador.dto.LoginResponseDTO;
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

/**
 * Service para autenticação e cadastro de usuários
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository;
    private final MatriculaRepository matriculaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository, AlunoRepository alunoRepository,
                       PlanoRepository planoRepository, MatriculaRepository matriculaRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.planoRepository = planoRepository;
        this.matriculaRepository = matriculaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Realiza o login do usuário
     */
    public LoginResponseDTO login(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Email ou senha inválidos");
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getTipoUsuario());

        // Buscar nome do aluno se for tipo aluno
        String nome = usuario.getEmail();
        if ("aluno".equals(usuario.getTipoUsuario())) {
            Aluno aluno = alunoRepository.findAll().stream()
                    .filter(a -> a.getUsuario().getId().equals(usuario.getId()))
                    .findFirst()
                    .orElse(null);
            if (aluno != null) {
                nome = aluno.getNome();
            }
        }

        return new LoginResponseDTO(token, usuario.getTipoUsuario(), nome);
    }

    /**
     * Cadastra um novo aluno no sistema
     */
    @Transactional
    public void cadastrar(CadastroDTO dto) {
        // Validar duplicatas
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (alunoRepository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        // Buscar plano
        Plano plano = planoRepository.findByNome(dto.getPlano())
                .orElseThrow(() -> new RuntimeException("Plano não encontrado: " + dto.getPlano()));

        // Calcular IMC
        BigDecimal imc = dto.getPeso().divide(
                dto.getAltura().multiply(dto.getAltura()),
                2,
                RoundingMode.HALF_UP
        );

        // Classificar IMC
        String classificacaoImc = classificarImc(imc);

        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setTipoUsuario("aluno");
        usuario = usuarioRepository.save(usuario);

        // Criar aluno
        Aluno aluno = new Aluno();
        aluno.setUsuario(usuario);
        aluno.setNome(dto.getNome());
        aluno.setSexo(dto.getSexo());
        aluno.setIdade(dto.getIdade());
        aluno.setCpf(dto.getCpf());
        aluno.setPeso(dto.getPeso());
        aluno.setAltura(dto.getAltura());
        aluno.setImc(imc);
        aluno.setClassificacaoImc(classificacaoImc);
        aluno = alunoRepository.save(aluno);

        // Calcular data de vencimento baseada no plano
        LocalDate dataInicio = LocalDate.now();
        LocalDate dataVencimento = calcularDataVencimento(dataInicio, dto.getPlano());

        // Criar matrícula
        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setPlano(plano);
        matricula.setStatus("Ativo");
        matricula.setDataInicio(dataInicio);
        matricula.setDataVencimento(dataVencimento);
        matriculaRepository.save(matricula);
    }

    /**
     * Classifica o IMC baseado nos valores padrão
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
     * Calcula a data de vencimento baseada no tipo de plano
     */
    private LocalDate calcularDataVencimento(LocalDate dataInicio, String plano) {
        return switch (plano) {
            case "Anual" -> dataInicio.plusYears(1);
            case "Mensal" -> dataInicio.plusMonths(1);
            case "Diária" -> dataInicio.plusDays(1);
            default -> dataInicio.plusMonths(1);
        };
    }
}
