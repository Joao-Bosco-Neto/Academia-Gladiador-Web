package br.com.gladiador.dto;

/**
 * DTO para resposta de login com token JWT
 */
public class LoginResponseDTO {

    private String token;
    private String tipoUsuario;
    private String nome;

    // Construtores
    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, String tipoUsuario, String nome) {
        this.token = token;
        this.tipoUsuario = tipoUsuario;
        this.nome = nome;
    }

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
