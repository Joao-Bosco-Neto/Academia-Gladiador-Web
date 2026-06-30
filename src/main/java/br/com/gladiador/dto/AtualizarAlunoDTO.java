package br.com.gladiador.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO para atualização de alunos existentes
 * Senha é opcional - se vazia, mantém a atual
 */
public class AtualizarAlunoDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Sexo é obrigatório")
    @Pattern(regexp = "^[MF]$", message = "Sexo deve ser M ou F")
    private String sexo;

    @NotNull(message = "Idade é obrigatória")
    @Positive(message = "Idade deve ser positiva")
    private Integer idade;

    @NotNull(message = "Peso é obrigatório")
    @Positive(message = "Peso deve ser positivo")
    private BigDecimal peso;

    @NotNull(message = "Altura é obrigatória")
    @Positive(message = "Altura deve ser positiva")
    private BigDecimal altura;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    private String cpf;

    // Senha é opcional na atualização - se vazia ou null, mantém a atual
    // Validação: permite vazia (min = 0) mas se fornecida, lógica de negócio valida tamanho mínimo
    private String senha;

    @NotBlank(message = "Plano é obrigatório")
    private String plano; // "Anual", "Mensal" ou "Diária"

    // Construtores
    public AtualizarAlunoDTO() {
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public void setAltura(BigDecimal altura) {
        this.altura = altura;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPlano() {
        return plano;
    }

    public void setPlano(String plano) {
        this.plano = plano;
    }
}
