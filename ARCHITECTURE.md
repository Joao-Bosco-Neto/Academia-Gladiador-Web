# Arquitetura do Sistema Academia Gladiador

## Visão Geral

Este é um sistema web RESTful desenvolvido com Spring Boot que segue o padrão de arquitetura em camadas (Layered Architecture) e os princípios SOLID.

## Arquitetura em Camadas

```
┌─────────────────────────────────────┐
│         Controllers                 │  ← Camada de Apresentação
│   (AuthController, AlunoController) │
└────────────────┬────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────┐
│          Services                   │  ← Camada de Lógica de Negócio
│   (AuthService, AlunoService)       │
└────────────────┬────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────┐
│        Repositories                 │  ← Camada de Acesso a Dados
│  (JPA Repositories)                 │
└────────────────┬────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────┐
│         Database (MySQL)            │  ← Camada de Persistência
└─────────────────────────────────────┘
```

## Componentes Principais

### 1. Models (Entidades JPA)

**Responsabilidade:** Representar as tabelas do banco de dados como objetos Java.

- `Usuario.java`: Usuários do sistema (admin/aluno)
- `Plano.java`: Planos de assinatura
- `Aluno.java`: Dados cadastrais dos alunos
- `Matricula.java`: Relação entre alunos e planos

**Tecnologias:**
- JPA Annotations (@Entity, @Table, @ManyToOne, etc.)
- Jakarta Persistence
- Hibernate (implementação JPA)

### 2. DTOs (Data Transfer Objects)

**Responsabilidade:** Transferir dados entre camadas sem expor as entidades diretamente.

- `LoginDTO`: Recebe credenciais de login
- `LoginResponseDTO`: Retorna token JWT e dados do usuário
- `CadastroDTO`: Recebe dados para cadastro de aluno
- `AlunoDTO`: Retorna dados completos do aluno (view vw_alunos)

**Benefícios:**
- Segurança: não expõe senhas nos retornos
- Flexibilidade: campos diferentes das entidades
- Validação: annotations do Bean Validation

### 3. Repositories

**Responsabilidade:** Interface com o banco de dados usando Spring Data JPA.

- `UsuarioRepository`: CRUD de usuários
- `PlanoRepository`: CRUD de planos
- `AlunoRepository`: CRUD de alunos + busca customizada
- `MatriculaRepository`: CRUD de matrículas

**Tecnologias:**
- Spring Data JPA
- Query Methods
- JPQL para queries customizadas

### 4. Services

**Responsabilidade:** Implementar a lógica de negócio da aplicação.

#### AuthService
- Login com validação de credenciais
- Geração de token JWT
- Cadastro de novos alunos
- Cálculo de IMC
- Validação de duplicatas (email, CPF)

#### AlunoService
- Listagem e busca de alunos
- Atualização de dados
- Exclusão de alunos
- Alteração de status de matrícula
- Conversão de entidades para DTOs

**Características:**
- `@Transactional`: garante atomicidade das operações
- Regras de negócio centralizadas
- Tratamento de exceções

### 5. Controllers

**Responsabilidade:** Expor endpoints REST e gerenciar requisições HTTP.

#### AuthController
- `POST /auth/login`: Autenticação
- `POST /auth/cadastro`: Registro de aluno

#### AlunoController
- `GET /alunos`: Listar todos
- `GET /alunos/{id}`: Buscar por ID
- `GET /alunos/buscar?termo=`: Buscar por nome/CPF
- `PUT /alunos/{id}`: Atualizar
- `DELETE /alunos/{id}`: Deletar
- `PATCH /alunos/{id}/status`: Alterar status

**Características:**
- Validação de entrada com @Valid
- Tratamento de exceções com try-catch
- Respostas padronizadas (HTTP status + JSON)
- Documentação Swagger/OpenAPI

### 6. Configuration

**Responsabilidade:** Configurar componentes do Spring.

#### SecurityConfig
- Desabilita CSRF (API REST stateless)
- Define endpoints públicos vs protegidos
- Configura filtro JWT
- Bean de BCryptPasswordEncoder

#### JwtUtil
- Geração de tokens JWT
- Extração de claims do token
- Validação de tokens
- Configuração de expiração (24h)

#### JwtAuthenticationFilter
- Intercepta requisições HTTP
- Valida token JWT no header Authorization
- Adiciona autenticação no SecurityContext
- Define roles (ROLE_ADMIN, ROLE_ALUNO)

#### SwaggerConfig
- Configuração do OpenAPI 3
- Documentação da API
- Suporte a autenticação Bearer Token

## Fluxo de Requisição

### Exemplo: Login de Usuário

```
1. Cliente → POST /auth/login {email, senha}
            ↓
2. AuthController.login(@RequestBody LoginDTO)
            ↓
3. AuthService.login(dto)
   - Busca usuário por email (UsuarioRepository)
   - Valida senha com BCrypt
   - Gera token JWT (JwtUtil)
   - Retorna LoginResponseDTO
            ↓
4. Controller retorna ResponseEntity<LoginResponseDTO>
            ↓
5. Cliente ← 200 OK {token, tipoUsuario, nome}
```

### Exemplo: Listar Alunos (Autenticado)

```
1. Cliente → GET /alunos
            Header: Authorization: Bearer {token}
            ↓
2. JwtAuthenticationFilter
   - Valida token
   - Extrai claims (email, tipoUsuario)
   - Adiciona autenticação no SecurityContext
            ↓
3. SecurityConfig
   - Verifica se tem ROLE_ADMIN
   - Permite acesso ao endpoint
            ↓
4. AlunoController.listarTodos()
            ↓
5. AlunoService.listarTodos()
   - Busca todos os alunos (AlunoRepository)
   - Para cada aluno:
     * Busca matrícula (MatriculaRepository)
     * Converte para AlunoDTO
   - Retorna List<AlunoDTO>
            ↓
6. Controller retorna ResponseEntity<List<AlunoDTO>>
            ↓
7. Cliente ← 200 OK [{aluno1}, {aluno2}, ...]
```

## Segurança

### Autenticação JWT

1. Usuário faz login com email e senha
2. Sistema valida credenciais
3. Sistema gera token JWT com claims:
   - `email`: identificação do usuário
   - `tipo_usuario`: admin ou aluno
   - `exp`: expiração (24h)
4. Token é assinado com chave secreta (HMAC-SHA256)
5. Cliente envia token em requisições futuras
6. Filtro JWT valida token antes de processar requisição

### Autorização

- **Endpoints Públicos:**
  - `/auth/login`
  - `/auth/cadastro`
  - `/swagger-ui/**`
  - `/api-docs/**`

- **Endpoints Protegidos:**
  - `/alunos/**` (apenas ROLE_ADMIN)

### Criptografia de Senhas

- BCrypt com salt automático
- Custo: 10 rounds (padrão)
- Não reversível (hash one-way)

## Banco de Dados

### Relacionamentos

```
usuarios (1) ──< (N) alunos
planos (1) ──< (N) matriculas
alunos (1) ──< (N) matriculas
```

### Cascade

- Deletar `usuario` → deleta `aluno` CASCADE
- Deletar `aluno` → deleta `matricula` CASCADE

### View vw_alunos

Consolida dados de 4 tabelas para facilitar consultas:
- usuarios (email, tipo_usuario)
- alunos (dados pessoais, IMC)
- planos (nome do plano, valor)
- matriculas (status, datas)

## Validações

### Bean Validation (DTOs)

- `@NotBlank`: campos obrigatórios
- `@Email`: formato de email válido
- `@Positive`: números positivos
- `@Pattern`: regex customizado (sexo, CPF)
- `@Size`: tamanho mínimo/máximo

### Validações de Negócio (Services)

- Email duplicado
- CPF duplicado
- Plano válido
- Status válido (Ativo/Inativo)

## Regras de Negócio

### Cálculo de IMC

```java
IMC = peso / (altura × altura)
Arredondamento: 2 casas decimais
```

### Classificação de IMC

| IMC          | Classificação     |
|--------------|-------------------|
| < 18.5       | Abaixo do peso    |
| 18.5 - 24.9  | Peso normal       |
| 25.0 - 29.9  | Sobrepeso         |
| ≥ 30.0       | Obesidade         |

### Cálculo de Vencimento

- **Anual**: data_inicio + 1 ano
- **Mensal**: data_inicio + 1 mês
- **Diária**: data_inicio + 1 dia

## Tratamento de Erros

### Estratégia

- Try-catch nos controllers
- Retorno de Map<String, String> com chave "erro"
- HTTP Status apropriado:
  - 200: Sucesso
  - 201: Criado
  - 400: Bad Request (dados inválidos)
  - 401: Unauthorized (credenciais inválidas)
  - 404: Not Found (recurso não encontrado)

### Exemplo

```json
{
  "erro": "Email já cadastrado"
}
```

## Tecnologias e Frameworks

| Tecnologia              | Versão  | Uso                          |
|-------------------------|---------|------------------------------|
| Java                    | 17      | Linguagem base               |
| Spring Boot             | 3.2.0   | Framework principal          |
| Spring Data JPA         | 3.2.0   | ORM e acesso a dados         |
| Spring Security         | 3.2.0   | Autenticação e autorização   |
| Hibernate               | 6.x     | Implementação JPA            |
| MySQL Connector         | 8.x     | Driver JDBC                  |
| JJWT                    | 0.11.5  | Geração e validação JWT      |
| SpringDoc OpenAPI       | 2.5.0   | Documentação Swagger         |
| Bean Validation         | 3.0     | Validação de dados           |
| Maven                   | 3.x     | Gerenciamento de dependências|

## Padrões de Projeto Utilizados

1. **Layered Architecture**: Separação em camadas
2. **Repository Pattern**: Abstração de acesso a dados
3. **DTO Pattern**: Transferência de dados entre camadas
4. **Dependency Injection**: Injeção via construtor
5. **Filter Pattern**: JwtAuthenticationFilter
6. **Factory Pattern**: BCryptPasswordEncoder bean

## Boas Práticas Implementadas

- ✅ Separação de responsabilidades (SRP)
- ✅ Injeção de dependências via construtor
- ✅ DTOs para não expor entidades
- ✅ Transações em operações multi-tabela
- ✅ Validação em múltiplas camadas
- ✅ Tratamento de exceções adequado
- ✅ Senhas nunca retornadas em responses
- ✅ Tokens JWT com expiração
- ✅ Documentação com Swagger
- ✅ Código em português (conforme requisito)

## Possíveis Melhorias Futuras

1. **Exception Handling Global**: @ControllerAdvice
2. **Paginação**: Pageable nos endpoints de listagem
3. **Auditoria**: CreatedBy, LastModifiedBy
4. **Testes Automatizados**: JUnit, Mockito
5. **Logs Estruturados**: SLF4J, Logback
6. **Refresh Tokens**: Renovação de token sem re-login
7. **Rate Limiting**: Controle de requisições por IP
8. **CORS Configuration**: Para frontend separado
9. **Docker**: Containerização da aplicação
10. **CI/CD**: Pipeline de integração contínua
