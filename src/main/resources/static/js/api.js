/**
 * ACADEMIA GLADIADOR - Módulo de API
 * Funções base para comunicação com o backend
 */

const API_BASE_URL = '';

/**
 * Realiza uma requisição fetch com tratamento de erros padrão
 */
async function apiFetch(url, options = {}) {
    try {
        const response = await fetch(API_BASE_URL + url, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        });

        const data = await response.json();

        if (!response.ok) {
            // Backend sempre retorna erro na chave "erro"
            throw new Error(data.erro || 'Erro na requisição');
        }

        return { success: true, data };
    } catch (error) {
        return { success: false, erro: error.message };
    }
}

/**
 * Retorna headers de autenticação com o token JWT
 */
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

/**
 * Verifica se o usuário está autenticado
 */
function verificarAutenticacao() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/index.html';
        return false;
    }
    return true;
}

/**
 * Verifica se o usuário é admin
 */
function verificarAdmin() {
    const tipoUsuario = localStorage.getItem('tipoUsuario');
    if (tipoUsuario !== 'admin') {
        window.location.href = '/aluno.html';
        return false;
    }
    return true;
}

/**
 * Formata valor monetário em Real brasileiro
 */
function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(valor);
}

/**
 * Formata data no padrão brasileiro (dd/mm/yyyy)
 */
function formatarData(dataString) {
    if (!dataString) return '';
    const data = new Date(dataString + 'T00:00:00');
    return data.toLocaleDateString('pt-BR');
}

/**
 * Converte sexo do formato extenso (Masculino/Feminino) para M/F
 */
function converterSexoParaCodigo(sexoExtenso) {
    if (sexoExtenso === 'Masculino') return 'M';
    if (sexoExtenso === 'Feminino') return 'F';
    return sexoExtenso; // Já está em formato M/F
}

/**
 * Remove pontuação do CPF (mantém apenas dígitos)
 */
function limparCPF(cpf) {
    return cpf.replace(/\D/g, '');
}

/**
 * Mostra mensagem de erro
 */
function mostrarErro(elementoId, mensagem) {
    const elemento = document.getElementById(elementoId);
    if (elemento) {
        elemento.textContent = mensagem;
        elemento.classList.add('ativo');
        setTimeout(() => {
            elemento.classList.remove('ativo');
        }, 5000);
    }
}

/**
 * Mostra mensagem de sucesso
 */
function mostrarSucesso(elementoId, mensagem) {
    const elemento = document.getElementById(elementoId);
    if (elemento) {
        elemento.textContent = mensagem;
        elemento.classList.add('ativo');
        setTimeout(() => {
            elemento.classList.remove('ativo');
        }, 5000);
    }
}

/**
 * Faz logout e redireciona para a tela de login
 */
function logout() {
    localStorage.clear();
    window.location.href = '/index.html';
}
