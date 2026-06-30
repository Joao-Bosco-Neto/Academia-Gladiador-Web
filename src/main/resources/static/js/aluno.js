/**
 * ACADEMIA GLADIADOR - Tela do Aluno
 * Exibe os dados do próprio aluno autenticado
 */

// Verificar autenticação ao carregar a página
if (!verificarAutenticacao()) {
    // Já redireciona para login dentro da função
}

// Carregar dados do aluno ao inicializar
document.addEventListener('DOMContentLoaded', carregarDadosAluno);

/**
 * Busca e exibe os dados do aluno autenticado
 */
async function carregarDadosAluno() {
    try {
        const response = await fetch('/alunos/me', {
            method: 'GET',
            headers: getAuthHeaders()
        });

        if (response.status === 401 || response.status === 403) {
            // Token inválido ou expirado
            logout();
            return;
        }

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.erro || 'Erro ao carregar dados');
        }

        // Atualiza nome no título
        document.getElementById('nomeAluno').textContent = data.nome.toUpperCase();

        // Renderiza os cards
        renderizarCards(data);

    } catch (erro) {
        mostrarErro('mensagemErro', erro.message);
    }
}

/**
 * Renderiza os cards com os dados do aluno
 */
function renderizarCards(aluno) {
    const container = document.getElementById('cardsContainer');

    // Determinar classe do card de IMC baseado na classificação
    let classeIMC = 'card-imc-normal';
    if (aluno.classificacaoImc === 'Abaixo do peso') {
        classeIMC = 'card-imc-baixo';
    } else if (aluno.classificacaoImc === 'Sobrepeso') {
        classeIMC = 'card-imc-sobre';
    } else if (aluno.classificacaoImc === 'Obesidade') {
        classeIMC = 'card-imc-obeso';
    }

    // Determinar classe do card de status
    const classeStatus = aluno.status === 'Ativo' ? 'card-ativo' : 'card-inativo';

    container.innerHTML = `
        <!-- Card Plano -->
        <div class="card">
            <div class="card-titulo">Plano</div>
            <div class="card-valor">${aluno.plano}</div>
        </div>

        <!-- Card Mensalidade -->
        <div class="card">
            <div class="card-titulo">Mensalidade</div>
            <div class="card-valor">${formatarMoeda(aluno.mensalidade)}</div>
        </div>

        <!-- Card IMC -->
        <div class="card ${classeIMC}">
            <div class="card-titulo">IMC</div>
            <div class="card-valor">${aluno.imc}</div>
            <div class="card-sub">${aluno.classificacaoImc}</div>
        </div>

        <!-- Card Status -->
        <div class="card ${classeStatus}">
            <div class="card-titulo">Status</div>
            <div class="card-valor">${aluno.status}</div>
        </div>

        <!-- Card Vencimento -->
        <div class="card">
            <div class="card-titulo">Data de Vencimento</div>
            <div class="card-valor" style="font-size: 18px;">${formatarData(aluno.dataVencimento)}</div>
        </div>
    `;
}
