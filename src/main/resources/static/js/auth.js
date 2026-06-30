/**
 * ACADEMIA GLADIADOR - Módulo de Autenticação
 * Funções de login e cadastro
 */

/**
 * Realiza o login do usuário
 * @param {string} email - Email do usuário
 * @param {string} senha - Senha do usuário
 * @returns {Promise<Object>} - Dados do login (token, tipoUsuario, nome)
 */
async function login(email, senha) {
    const response = await fetch('/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, senha })
    });

    const data = await response.json();

    if (!response.ok) {
        // Backend retorna erro na chave "erro"
        throw new Error(data.erro || 'Erro ao fazer login');
    }

    // Armazena dados no localStorage
    localStorage.setItem('token', data.token);
    localStorage.setItem('tipoUsuario', data.tipoUsuario);
    localStorage.setItem('nome', data.nome);

    return data;
}

/**
 * Realiza o cadastro de um novo aluno
 * @param {Object} dadosCadastro - Dados do cadastro (CadastroDTO)
 * @returns {Promise<Object>} - Resposta do backend
 */
async function cadastrar(dadosCadastro) {
    const response = await fetch('/auth/cadastro', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(dadosCadastro)
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.erro || 'Erro ao realizar cadastro');
    }

    return data;
}
