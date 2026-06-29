# Guia de Testes da API Academia Gladiador

## 1. Iniciar a Aplicação

```bash
mvn spring-boot:run
```

Aguarde a mensagem: `Started AcademiaGladiadorApplication`

## 2. Acessar o Swagger

Abra no navegador: `http://localhost:8080/swagger-ui.html`

## 3. Testes Passo a Passo

### 3.1. Cadastrar um Novo Aluno

**Endpoint:** `POST /auth/cadastro`

**Body (JSON):**
```json
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

**Resposta esperada (201):**
```json
{
  "mensagem": "Cadastro realizado com sucesso"
}
```

### 3.2. Fazer Login como Admin

**Endpoint:** `POST /auth/login`

**Body (JSON):**
```json
{
  "email": "admin@gladiador.com",
  "senha": "admin123"
}
```

**Resposta esperada (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipoUsuario": "admin",
  "nome": "admin@gladiador.com"
}
```

**⚠️ IMPORTANTE:** Copie o token retornado!

### 3.3. Configurar Autenticação no Swagger

1. Clique no botão **"Authorize"** no topo da página do Swagger
2. Cole o token copiado no campo **"Value"** (sem adicionar "Bearer")
3. Clique em **"Authorize"** e depois **"Close"**

### 3.4. Listar Todos os Alunos

**Endpoint:** `GET /alunos`

**Headers:**
```
Authorization: Bearer {seu_token}
```

**Resposta esperada (200):** Lista de alunos em formato JSON

### 3.5. Buscar Aluno por ID

**Endpoint:** `GET /alunos/{id}`

**Exemplo:** `GET /alunos/1`

**Resposta esperada (200):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "sexo": "M",
  "idade": 25,
  "cpf": "12345678901",
  "peso": 75.5,
  "altura": 1.75,
  "imc": 24.65,
  "classificacaoImc": "Peso normal",
  "email": "joao@email.com",
  "tipoUsuario": "aluno",
  "plano": "Mensal",
  "mensalidade": 129.00,
  "status": "Ativo",
  "dataInicio": "2026-06-29",
  "dataVencimento": "2026-07-29",
  "dataCadastro": "2026-06-29T10:30:00"
}
```

### 3.6. Buscar Aluno por Nome ou CPF

**Endpoint:** `GET /alunos/buscar?termo=João`

**Resposta esperada (200):** Lista de alunos que correspondem ao termo

### 3.7. Atualizar Dados do Aluno

**Endpoint:** `PUT /alunos/{id}`

**Exemplo:** `PUT /alunos/1`

**Body (JSON):**
```json
{
  "nome": "João Silva Santos",
  "sexo": "M",
  "idade": 26,
  "peso": 78.0,
  "altura": 1.75,
  "email": "joao@email.com",
  "cpf": "12345678901",
  "senha": "novaSenha123",
  "plano": "Anual"
}
```

**Resposta esperada (200):**
```json
{
  "mensagem": "Aluno atualizado com sucesso"
}
```

### 3.8. Alterar Status da Matrícula

**Endpoint:** `PATCH /alunos/{id}/status`

**Exemplo:** `PATCH /alunos/1/status`

**Body (JSON):**
```json
{
  "status": "Inativo"
}
```

**Resposta esperada (200):**
```json
{
  "mensagem": "Status alterado com sucesso"
}
```

### 3.9. Deletar Aluno

**Endpoint:** `DELETE /alunos/{id}`

**Exemplo:** `DELETE /alunos/1`

**Resposta esperada (200):**
```json
{
  "mensagem": "Aluno deletado com sucesso"
}
```

## 4. Testes com cURL (Terminal)

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@gladiador.com","senha":"admin123"}'
```

### Cadastro
```bash
curl -X POST http://localhost:8080/auth/cadastro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "sexo": "F",
    "idade": 28,
    "peso": 65.0,
    "altura": 1.65,
    "email": "maria@email.com",
    "cpf": "98765432100",
    "senha": "senha123",
    "plano": "Anual"
  }'
```

### Listar Alunos (substitua SEU_TOKEN pelo token obtido)
```bash
curl -X GET http://localhost:8080/alunos \
  -H "Authorization: Bearer SEU_TOKEN"
```

## 5. Possíveis Erros

### 401 Unauthorized
- Token inválido ou expirado
- Solução: Faça login novamente e obtenha um novo token

### 400 Bad Request
- Dados inválidos no body da requisição
- Email ou CPF duplicado
- Solução: Verifique os dados enviados

### 404 Not Found
- Aluno não encontrado
- Solução: Verifique se o ID existe

### 403 Forbidden
- Usuário não tem permissão (não é admin)
- Solução: Faça login com usuário admin

## 6. Dados de Teste Padrão

### Usuário Admin
- **Email:** admin@gladiador.com
- **Senha:** admin123

### Planos Disponíveis
- **Anual** - R$ 99,00
- **Mensal** - R$ 129,00
- **Diária** - R$ 20,00

## 7. Validações Implementadas

- Email: formato válido
- CPF: 11 dígitos numéricos
- Senha: mínimo 6 caracteres
- Sexo: apenas "M" ou "F"
- Idade, Peso, Altura: valores positivos
- Status: apenas "Ativo" ou "Inativo"

## 8. Cálculos Automáticos

### IMC
- Fórmula: peso / (altura × altura)
- Arredondamento: 2 casas decimais

### Classificação IMC
- < 18.5: Abaixo do peso
- 18.5 - 24.9: Peso normal
- 25.0 - 29.9: Sobrepeso
- ≥ 30.0: Obesidade

### Data de Vencimento
- Anual: +1 ano
- Mensal: +1 mês
- Diária: +1 dia
