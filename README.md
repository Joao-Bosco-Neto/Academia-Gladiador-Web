# Academia Gladiador - API REST

Sistema de gerenciamento de academia desenvolvido com Spring Boot 3, MySQL e JWT.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **MySQL 8**
- **JWT (JSON Web Token)**
- **Swagger/OpenAPI 3**
- **Maven**

## Pré-requisitos

1. Java 17 instalado
2. Maven instalado
3. MySQL 8 instalado e rodando
4. Banco de dados `academia_db` criado e configurado

## Configuração

1. Clone o repositório
2. Configure o banco de dados no arquivo `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=sua_senha_aqui
   ```

## Como Executar

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## Documentação da API (Swagger)

Acesse a documentação interativa em: `http://localhost:8080/swagger-ui.html`

## Endpoints Principais

### Autenticação (Público)

- **POST** `/auth/login` - Realizar login
- **POST** `/auth/cadastro` - Cadastrar novo aluno

### Gerenciamento de Alunos (Requer autenticação admin)

- **GET** `/alunos` - Listar todos os alunos
- **GET** `/alunos/{id}` - Buscar aluno por ID
- **GET** `/alunos/buscar?termo={termo}` - Buscar por nome ou CPF
- **PUT** `/alunos/{id}` - Atualizar dados do aluno
- **DELETE** `/alunos/{id}` - Deletar aluno
- **PATCH** `/alunos/{id}/status` - Alterar status da matrícula

## Autenticação

A API utiliza JWT (JSON Web Token) para autenticação. Para acessar endpoints protegidos:

1. Faça login em `/auth/login` e obtenha o token
2. Adicione o token no header das requisições: `Authorization: Bearer {seu_token}`
3. No Swagger, clique em "Authorize" e insira o token

## Planos Disponíveis

- **Anual**: R$ 99,00
- **Mensal**: R$ 129,00
- **Diária**: R$ 20,00

## Exemplo de Requisição de Login

```json
POST /auth/login
{
  "email": "admin@gladiador.com",
  "senha": "senha123"
}
```

## Exemplo de Resposta de Login

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipoUsuario": "admin",
  "nome": "Administrador"
}
```

## Exemplo de Cadastro de Aluno

```json
POST /auth/cadastro
{
  "nome": "João Silva",
  "sexo": "M",
  "idade": 25,
  "peso": 75.5,
  "altura": 1.75,
  "email": "joao@email.com",
  "cpf": "12345678901",
  "senha": "senha123",
  "plano": "Mensal"
}
```

## Estrutura do Projeto

```
src/main/java/br/com/gladiador/
├── AcademiaGladiadorApplication.java
├── config/
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthController.java
│   └── AlunoController.java
├── dto/
│   ├── LoginDTO.java
│   ├── LoginResponseDTO.java
│   ├── CadastroDTO.java
│   └── AlunoDTO.java
├── model/
│   ├── Usuario.java
│   ├── Plano.java
│   ├── Aluno.java
│   └── Matricula.java
├── repository/
│   ├── UsuarioRepository.java
│   ├── PlanoRepository.java
│   ├── AlunoRepository.java
│   └── MatriculaRepository.java
└── service/
    ├── AuthService.java
    └── AlunoService.java
```

## Regras de Negócio

### Cálculo do IMC
- IMC = peso / (altura × altura)
- Resultado arredondado para 2 casas decimais

### Classificação do IMC
- **< 18.5**: Abaixo do peso
- **18.5 - 24.9**: Peso normal
- **25.0 - 29.9**: Sobrepeso
- **≥ 30.0**: Obesidade

### Vencimento dos Planos
- **Anual**: +1 ano a partir da data de início
- **Mensal**: +1 mês a partir da data de início
- **Diária**: +1 dia a partir da data de início

## Segurança

- Senhas criptografadas com BCrypt
- Autenticação via JWT com expiração de 24 horas
- Endpoints de administração protegidos (apenas role ADMIN)
- CSRF desabilitado (API REST stateless)

## Autor

Sistema desenvolvido para Academia Gladiador

## Licença

Este projeto é privado e de uso exclusivo da Academia Gladiador.