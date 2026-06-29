# Exemplos de Uso da API Academia Gladiador

Este arquivo contém exemplos práticos de como consumir a API em diferentes linguagens e ferramentas.

---

## 📋 Índice

1. [cURL (Terminal)](#curl-terminal)
2. [JavaScript (Fetch API)](#javascript-fetch-api)
3. [Python (Requests)](#python-requests)
4. [Java (RestTemplate)](#java-resttemplate)
5. [Postman](#postman)
6. [HTTPie](#httpie)

---

## 🔧 cURL (Terminal)

### Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@gladiador.com",
    "senha": "admin123"
  }'
```

### Cadastro

```bash
curl -X POST http://localhost:8080/auth/cadastro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "sexo": "M",
    "idade": 25,
    "peso": 75.5,
    "altura": 1.75,
    "email": "joao@email.com",
    "cpf": "12345678901",
    "senha": "senha123",
    "plano": "Mensal"
  }'
```

### Listar Alunos (com token)

```bash
TOKEN="seu_token_jwt_aqui"

curl -X GET http://localhost:8080/alunos \
  -H "Authorization: Bearer $TOKEN"
```

### Buscar Aluno por ID

```bash
curl -X GET http://localhost:8080/alunos/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Buscar por Nome/CPF

```bash
curl -X GET "http://localhost:8080/alunos/buscar?termo=João" \
  -H "Authorization: Bearer $TOKEN"
```

### Atualizar Aluno

```bash
curl -X PUT http://localhost:8080/alunos/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva Santos",
    "sexo": "M",
    "idade": 26,
    "peso": 78.0,
    "altura": 1.75,
    "email": "joao@email.com",
    "cpf": "12345678901",
    "senha": "novaSenha123",
    "plano": "Anual"
  }'
```

### Alterar Status

```bash
curl -X PATCH http://localhost:8080/alunos/1/status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status": "Inativo"}'
```

### Deletar Aluno

```bash
curl -X DELETE http://localhost:8080/alunos/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🟨 JavaScript (Fetch API)

### Login

```javascript
async function login(email, senha) {
  const response = await fetch('http://localhost:8080/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ email, senha }),
  });

  const data = await response.json();
  
  if (response.ok) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('tipoUsuario', data.tipoUsuario);
    localStorage.setItem('nome', data.nome);
    return data;
  } else {
    throw new Error(data.erro || 'Erro ao fazer login');
  }
}

// Uso
login('admin@gladiador.com', 'admin123')
  .then(data => console.log('Login sucesso:', data))
  .catch(err => console.error('Erro:', err.message));
```

### Cadastro

```javascript
async function cadastrar(dadosAluno) {
  const response = await fetch('http://localhost:8080/auth/cadastro', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(dadosAluno),
  });

  const data = await response.json();
  
  if (!response.ok) {
    throw new Error(data.erro || 'Erro ao cadastrar');
  }
  
  return data;
}

// Uso
const novoAluno = {
  nome: 'Maria Santos',
  sexo: 'F',
  idade: 28,
  peso: 65.0,
  altura: 1.65,
  email: 'maria@email.com',
  cpf: '98765432100',
  senha: 'senha123',
  plano: 'Anual'
};

cadastrar(novoAluno)
  .then(data => console.log('Cadastro sucesso:', data))
  .catch(err => console.error('Erro:', err.message));
```

### Listar Alunos

```javascript
async function listarAlunos() {
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8080/alunos', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    throw new Error('Erro ao listar alunos');
  }

  return await response.json();
}

// Uso
listarAlunos()
  .then(alunos => console.log('Alunos:', alunos))
  .catch(err => console.error('Erro:', err.message));
```

### Buscar Aluno por ID

```javascript
async function buscarAluno(id) {
  const token = localStorage.getItem('token');
  
  const response = await fetch(`http://localhost:8080/alunos/${id}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });

  const data = await response.json();
  
  if (!response.ok) {
    throw new Error(data.erro || 'Aluno não encontrado');
  }

  return data;
}

// Uso
buscarAluno(1)
  .then(aluno => console.log('Aluno:', aluno))
  .catch(err => console.error('Erro:', err.message));
```

### Cliente Completo com Classe

```javascript
class AcademiaGladiadorAPI {
  constructor(baseURL = 'http://localhost:8080') {
    this.baseURL = baseURL;
    this.token = localStorage.getItem('token');
  }

  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    if (this.token && !options.public) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    const response = await fetch(url, {
      ...options,
      headers,
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.erro || 'Erro na requisição');
    }

    return data;
  }

  async login(email, senha) {
    const data = await this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, senha }),
      public: true,
    });

    this.token = data.token;
    localStorage.setItem('token', data.token);
    return data;
  }

  async cadastrar(dadosAluno) {
    return this.request('/auth/cadastro', {
      method: 'POST',
      body: JSON.stringify(dadosAluno),
      public: true,
    });
  }

  async listarAlunos() {
    return this.request('/alunos');
  }

  async buscarAluno(id) {
    return this.request(`/alunos/${id}`);
  }

  async buscarPorTermo(termo) {
    return this.request(`/alunos/buscar?termo=${encodeURIComponent(termo)}`);
  }

  async atualizarAluno(id, dados) {
    return this.request(`/alunos/${id}`, {
      method: 'PUT',
      body: JSON.stringify(dados),
    });
  }

  async alterarStatus(id, status) {
    return this.request(`/alunos/${id}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    });
  }

  async deletarAluno(id) {
    return this.request(`/alunos/${id}`, {
      method: 'DELETE',
    });
  }

  logout() {
    this.token = null;
    localStorage.removeItem('token');
  }
}

// Uso
const api = new AcademiaGladiadorAPI();

// Login
await api.login('admin@gladiador.com', 'admin123');

// Listar alunos
const alunos = await api.listarAlunos();
console.log(alunos);

// Buscar aluno
const aluno = await api.buscarAluno(1);
console.log(aluno);
```

---

## 🐍 Python (Requests)

### Instalação

```bash
pip install requests
```

### Cliente Python Completo

```python
import requests
from typing import Dict, List, Optional

class AcademiaGladiadorAPI:
    def __init__(self, base_url: str = "http://localhost:8080"):
        self.base_url = base_url
        self.token: Optional[str] = None

    def _request(self, method: str, endpoint: str, data: Dict = None, 
                 params: Dict = None, public: bool = False):
        url = f"{self.base_url}{endpoint}"
        headers = {"Content-Type": "application/json"}
        
        if self.token and not public:
            headers["Authorization"] = f"Bearer {self.token}"
        
        response = requests.request(
            method=method,
            url=url,
            json=data,
            params=params,
            headers=headers
        )
        
        try:
            json_data = response.json()
        except:
            json_data = {}
        
        if not response.ok:
            erro = json_data.get("erro", "Erro na requisição")
            raise Exception(f"Erro {response.status_code}: {erro}")
        
        return json_data

    def login(self, email: str, senha: str) -> Dict:
        data = self._request(
            "POST", 
            "/auth/login", 
            data={"email": email, "senha": senha},
            public=True
        )
        self.token = data["token"]
        return data

    def cadastrar(self, dados_aluno: Dict) -> Dict:
        return self._request("POST", "/auth/cadastro", data=dados_aluno, public=True)

    def listar_alunos(self) -> List[Dict]:
        return self._request("GET", "/alunos")

    def buscar_aluno(self, id: int) -> Dict:
        return self._request("GET", f"/alunos/{id}")

    def buscar_por_termo(self, termo: str) -> List[Dict]:
        return self._request("GET", "/alunos/buscar", params={"termo": termo})

    def atualizar_aluno(self, id: int, dados: Dict) -> Dict:
        return self._request("PUT", f"/alunos/{id}", data=dados)

    def alterar_status(self, id: int, status: str) -> Dict:
        return self._request("PATCH", f"/alunos/{id}/status", data={"status": status})

    def deletar_aluno(self, id: int) -> Dict:
        return self._request("DELETE", f"/alunos/{id}")


# Uso
if __name__ == "__main__":
    api = AcademiaGladiadorAPI()
    
    # Login
    try:
        login_response = api.login("admin@gladiador.com", "admin123")
        print("Login bem-sucedido!")
        print(f"Token: {login_response['token'][:50]}...")
        print(f"Tipo: {login_response['tipoUsuario']}")
    except Exception as e:
        print(f"Erro no login: {e}")
        exit(1)
    
    # Cadastrar aluno
    novo_aluno = {
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
    
    try:
        api.cadastrar(novo_aluno)
        print("Aluno cadastrado com sucesso!")
    except Exception as e:
        print(f"Erro ao cadastrar: {e}")
    
    # Listar alunos
    try:
        alunos = api.listar_alunos()
        print(f"\nTotal de alunos: {len(alunos)}")
        for aluno in alunos:
            print(f"- {aluno['nome']} ({aluno['email']}) - {aluno['status']}")
    except Exception as e:
        print(f"Erro ao listar: {e}")
    
    # Buscar por termo
    try:
        resultado = api.buscar_por_termo("João")
        print(f"\nBusca por 'João': {len(resultado)} resultado(s)")
    except Exception as e:
        print(f"Erro na busca: {e}")
```

---

## ☕ Java (RestTemplate)

### Dependências (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### Cliente Java

```java
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

public class AcademiaGladiadorClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;
    private String token;

    public AcademiaGladiadorClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> login(String email, String senha) {
        String url = baseUrl + "/auth/login";
        
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("senha", senha);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        Map<String, Object> body = response.getBody();
        
        if (body != null && body.containsKey("token")) {
            this.token = (String) body.get("token");
        }
        
        return body;
    }

    public Map<String, Object> cadastrar(Map<String, Object> dadosAluno) {
        String url = baseUrl + "/auth/cadastro";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(dadosAluno, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        return response.getBody();
    }

    public Object[] listarAlunos() {
        String url = baseUrl + "/alunos";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Object[]> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            entity, 
            Object[].class
        );
        
        return response.getBody();
    }

    public Map<String, Object> buscarAluno(int id) {
        String url = baseUrl + "/alunos/" + id;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            entity, 
            Map.class
        );
        
        return response.getBody();
    }

    // Exemplo de uso
    public static void main(String[] args) {
        AcademiaGladiadorClient client = new AcademiaGladiadorClient("http://localhost:8080");
        
        // Login
        Map<String, Object> loginResponse = client.login("admin@gladiador.com", "admin123");
        System.out.println("Login: " + loginResponse);
        
        // Listar alunos
        Object[] alunos = client.listarAlunos();
        System.out.println("Total de alunos: " + alunos.length);
    }
}
```

---

## 📮 Postman

### Importar Collection

Crie um arquivo `postman_collection.json`:

```json
{
  "info": {
    "name": "Academia Gladiador API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"admin@gladiador.com\",\n  \"senha\": \"admin123\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{base_url}}/auth/login",
              "host": ["{{base_url}}"],
              "path": ["auth", "login"]
            }
          }
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080"
    }
  ]
}
```

---

## 🌐 HTTPie

### Instalação

```bash
pip install httpie
```

### Exemplos

```bash
# Login
http POST localhost:8080/auth/login email=admin@gladiador.com senha=admin123

# Cadastro
http POST localhost:8080/auth/cadastro \
  nome="João Silva" \
  sexo=M \
  idade:=25 \
  peso:=75.5 \
  altura:=1.75 \
  email=joao@email.com \
  cpf=12345678901 \
  senha=senha123 \
  plano=Mensal

# Listar alunos (com token)
http GET localhost:8080/alunos "Authorization:Bearer TOKEN_AQUI"
```

---

## 🎯 Boas Práticas

1. **Armazene o token com segurança** (localStorage, sessionStorage, cookies httpOnly)
2. **Implemente refresh token** para renovação automática
3. **Trate erros adequadamente** e mostre mensagens ao usuário
4. **Valide dados no frontend** antes de enviar
5. **Use HTTPS em produção**
6. **Não exponha o token** em logs ou console em produção

---

**Documentação completa:** `http://localhost:8080/swagger-ui.html`
