# 🚀 Guia Rápido - Academia Gladiador API

## ⚡ Início Rápido (5 minutos)

### 1️⃣ Pré-requisitos

```bash
# Verificar instalações
java -version    # Deve ser Java 17+
mvn -version     # Maven 3.x
mysql --version  # MySQL 8.x
```

### 2️⃣ Configurar Banco de Dados

```bash
# Conectar ao MySQL
mysql -u root -p

# Executar no MySQL
CREATE DATABASE academia_db;
USE academia_db;
SOURCE database-schema.sql;
SOURCE insert-test-data.sql;  # Opcional - dados de teste
EXIT;
```

### 3️⃣ Configurar Senha do MySQL

Edite: `src/main/resources/application.properties`

```properties
spring.datasource.password=SUA_SENHA_AQUI
```

### 4️⃣ Iniciar Aplicação

```bash
mvn spring-boot:run
```

Aguarde: `Started AcademiaGladiadorApplication in X seconds`

### 5️⃣ Testar

Abra no navegador: **http://localhost:8080/swagger-ui.html**

---

## 🧪 Teste Rápido

### 1. Fazer Login como Admin

**Endpoint:** `POST /auth/login`

```json
{
  "email": "admin@gladiador.com",
  "senha": "admin123"
}
```

**Copie o token retornado!**

### 2. Autorizar no Swagger

1. Clique em **"Authorize"** (cadeado no topo)
2. Cole o token
3. Clique **"Authorize"** → **"Close"**

### 3. Listar Alunos

**Endpoint:** `GET /alunos`

Clique em **"Try it out"** → **"Execute"**

---

## 📋 Comandos Úteis

```bash
# Compilar sem executar
mvn clean compile

# Executar testes
mvn test

# Gerar JAR
mvn clean package

# Executar JAR
java -jar target/academia-gladiador-1.0.0.jar

# Limpar target
mvn clean
```

---

## 🔑 Credenciais Padrão

| Tipo  | Email                    | Senha     |
|-------|--------------------------|-----------|
| Admin | admin@gladiador.com      | admin123  |
| Aluno | carlos@email.com         | senha123  |
| Aluno | ana@email.com            | senha123  |
| Aluno | pedro@email.com          | senha123  |

---

## 🌐 URLs Importantes

| Serviço    | URL                                      |
|------------|------------------------------------------|
| API        | http://localhost:8080                    |
| Swagger UI | http://localhost:8080/swagger-ui.html    |
| API Docs   | http://localhost:8080/api-docs           |

---

## 🐛 Troubleshooting

### Erro: "Cannot connect to database"

```properties
# Verifique application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/academia_db
spring.datasource.username=root
spring.datasource.password=SUA_SENHA
```

### Erro: "Port 8080 already in use"

```bash
# Opção 1: Matar processo na porta 8080
lsof -ti:8080 | xargs kill -9

# Opção 2: Mudar porta em application.properties
server.port=8081
```

### Erro: "Table doesn't exist"

```bash
# Executar scripts SQL novamente
mysql -u root -p academia_db < database-schema.sql
```

### Erro: "401 Unauthorized" no Swagger

1. Faça login em `/auth/login`
2. Copie o token
3. Clique em "Authorize" no Swagger
4. Cole o token (SEM "Bearer")
5. Clique "Authorize"

---

## 📚 Documentação Completa

- **README.md**: Visão geral e instruções
- **ARCHITECTURE.md**: Arquitetura detalhada
- **API-TESTS.md**: Exemplos de testes completos
- **database-schema.sql**: Script de criação do banco
- **insert-test-data.sql**: Dados de teste

---

## ✅ Checklist de Verificação

- [ ] Java 17 instalado
- [ ] Maven instalado
- [ ] MySQL rodando
- [ ] Banco `academia_db` criado
- [ ] Scripts SQL executados
- [ ] Senha configurada em `application.properties`
- [ ] Aplicação iniciada sem erros
- [ ] Swagger acessível
- [ ] Login funcionando
- [ ] Endpoints protegidos requerem token

---

## 💡 Dicas

1. **Use o Swagger**: Interface visual para testar todos os endpoints
2. **Valide os dados**: A API retorna mensagens de erro em português
3. **Token expira em 24h**: Faça login novamente se necessário
4. **Senhas são criptografadas**: Não é possível recuperar, apenas resetar
5. **IMC calculado automaticamente**: Baseado em peso e altura

---

## 🆘 Precisa de Ajuda?

1. Consulte **API-TESTS.md** para exemplos detalhados
2. Leia **ARCHITECTURE.md** para entender a estrutura
3. Verifique logs no console ao executar `mvn spring-boot:run`
4. Teste endpoints públicos primeiro (`/auth/login`, `/auth/cadastro`)

---

## 🎯 Próximos Passos

1. ✅ Teste todos os endpoints no Swagger
2. ✅ Cadastre novos alunos
3. ✅ Experimente buscar por nome/CPF
4. ✅ Altere status de matrículas
5. ✅ Explore a documentação completa

**Boa sorte com o projeto Academia Gladiador! 💪🏋️**
