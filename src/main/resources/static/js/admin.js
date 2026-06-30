/**
 * ACADEMIA GLADIADOR - Painel Administrativo
 * Gerenciamento completo de alunos (CRUD)
 */

// Verificar autenticação e permissão de admin
if (!verificarAutenticacao() || !verificarAdmin()) {
    // Já redireciona dentro das funções
}

// Variáveis globais
let alunoSelecionado = null;
let todosAlunos = [];

// Inicializar ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    carregarAlunos();
    configurarBusca();
    configurarCampoCPF();
});

/**
 * Configura o campo de busca com debounce
 */
function configurarBusca() {
    let timeoutBusca;
    const campoBusca = document.getElementById('campoBusca');

    campoBusca.addEventListener('input', (e) => {
        clearTimeout(timeoutBusca);
        const termo = e.target.value.trim();

        timeoutBusca = setTimeout(() => {
            if (termo.length >= 2) {
                buscarAlunos(termo);
            } else if (termo.length === 0) {
                carregarAlunos();
            }
        }, 500); // Debounce de 500ms
    });
}

/**
 * Aceitar apenas números no campo CPF
 */
function configurarCampoCPF() {
    document.getElementById('cpf').addEventListener('input', (e) => {
        e.target.value = e.target.value.replace(/\D/g, '');
    });
}

/**
 * Carrega todos os alunos
 */
async function carregarAlunos() {
    try {
        const response = await fetch('/alunos', {
            method: 'GET',
            headers: getAuthHeaders()
        });

        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.erro || 'Erro ao carregar alunos');
        }

        todosAlunos = data;
        renderizarTabela(todosAlunos);

    } catch (erro) {
        mostrarErro('mensagemErro', erro.message);
    }
}

/**
 * Busca alunos por termo (nome ou CPF)
 */
async function buscarAlunos(termo) {
    try {
        const response = await fetch(`/alunos/buscar?termo=${encodeURIComponent(termo)}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });

        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.erro || 'Erro ao buscar alunos');
        }

        todosAlunos = data;
        renderizarTabela(todosAlunos);

    } catch (erro) {
        mostrarErro('mensagemErro', erro.message);
    }
}

/**
 * Renderiza a tabela de alunos
 */
function renderizarTabela(alunos) {
    const tbody = document.getElementById('tabelaAlunos');

    if (alunos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 40px;">Nenhum aluno encontrado</td></tr>';
        return;
    }

    tbody.innerHTML = alunos.map(aluno => `
        <tr onclick="selecionarAluno(${aluno.id})" data-id="${aluno.id}">
            <td>${aluno.id}</td>
            <td>${aluno.nome}</td>
            <td>${aluno.plano}</td>
            <td>${aluno.imc} - ${aluno.classificacaoImc}</td>
            <td>${aluno.status}</td>
            <td>${aluno.tipoUsuario}</td>
        </tr>
    `).join('');
}

/**
 * Seleciona um aluno da tabela
 */
function selecionarAluno(id) {
    // Remove seleção anterior
    document.querySelectorAll('.tabela tbody tr').forEach(tr => {
        tr.classList.remove('selecionado');
    });

    // Adiciona seleção na linha clicada
    const linha = document.querySelector(`tr[data-id="${id}"]`);
    linha.classList.add('selecionado');

    // Busca dados do aluno
    alunoSelecionado = todosAlunos.find(a => a.id === id);

    if (alunoSelecionado) {
        preencherFormulario(alunoSelecionado);
        habilitarBotoes();
    }
}

/**
 * Preenche o formulário com os dados do aluno selecionado
 */
function preencherFormulario(aluno) {
    document.getElementById('alunoId').value = aluno.id;
    document.getElementById('nome').value = aluno.nome;

    // Converter sexo extenso para código (Masculino/Feminino -> M/F)
    document.getElementById('sexo').value = converterSexoParaCodigo(aluno.sexo);

    document.getElementById('idade').value = aluno.idade;
    document.getElementById('peso').value = aluno.peso;
    document.getElementById('altura').value = aluno.altura;
    document.getElementById('email').value = aluno.email;
    document.getElementById('cpf').value = aluno.cpf;
    document.getElementById('senha').value = ''; // Senha sempre vazia
    document.getElementById('plano').value = aluno.plano;
}

/**
 * Limpa o formulário e desabilita botões
 */
function limparFormulario() {
    document.getElementById('formAluno').reset();
    document.getElementById('alunoId').value = '';
    alunoSelecionado = null;

    // Remove seleção da tabela
    document.querySelectorAll('.tabela tbody tr').forEach(tr => {
        tr.classList.remove('selecionado');
    });

    desabilitarBotoes();
}

/**
 * Habilita botões de ação
 */
function habilitarBotoes() {
    document.getElementById('btnAtualizar').disabled = false;
    document.getElementById('btnExcluir').disabled = false;
    document.getElementById('btnStatus').disabled = false;
}

/**
 * Desabilita botões de ação
 */
function desabilitarBotoes() {
    document.getElementById('btnAtualizar').disabled = true;
    document.getElementById('btnExcluir').disabled = true;
    document.getElementById('btnStatus').disabled = true;
}

/**
 * Adiciona um novo aluno
 */
async function adicionarAluno() {
    const form = document.getElementById('formAluno');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    // Coletar dados do formulário
    const dadosCadastro = {
        nome: document.getElementById('nome').value.trim(),
        sexo: document.getElementById('sexo').value,
        idade: parseInt(document.getElementById('idade').value),
        peso: parseFloat(document.getElementById('peso').value),
        altura: parseFloat(document.getElementById('altura').value),
        email: document.getElementById('email').value.trim(),
        cpf: limparCPF(document.getElementById('cpf').value),
        senha: document.getElementById('senha').value,
        plano: document.getElementById('plano').value
    };

    // Validar CPF
    if (dadosCadastro.cpf.length !== 11) {
        mostrarErro('mensagemErro', 'CPF deve ter exatamente 11 dígitos numéricos');
        return;
    }

    // Validar senha (obrigatória no cadastro)
    if (!dadosCadastro.senha || dadosCadastro.senha.length < 6) {
        mostrarErro('mensagemErro', 'Senha deve ter no mínimo 6 caracteres');
        return;
    }

    try {
        const response = await fetch('/auth/cadastro', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosCadastro)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.erro || 'Erro ao adicionar aluno');
        }

        mostrarSucesso('mensagemSucesso', data.mensagem || 'Aluno adicionado com sucesso');
        limparFormulario();
        carregarAlunos();

    } catch (erro) {
        mostrarErro('mensagemErro', erro.message);
    }
}

/**
 * Atualiza o aluno selecionado
 */
async function atualizarAluno() {
    if (!alunoSelecionado) {
        mostrarErro('mensagemErro', 'Selecione um aluno na tabela');
        return;
    }

    const form = document.getElementById('formAluno');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    // Coletar dados do formulário
    const dadosAtualizacao = {
        nome: document.getElementById('nome').value.trim(),
        sexo: document.getElementById('sexo').value,
        idade: parseInt(document.getElementById('idade').value),
        peso: parseFloat(document.getElementById('peso').value),
        altura: parseFloat(document.getElementById('altura').value),
        email: document.getElementById('email').value.trim(),
        cpf: limparCPF(document.getElementById('cpf').value),
        senha: document.getElementById('senha').value, // Pode ser vazio (mantém a atual)
        plano: document.getElementById('plano').value
    };

    // Validar CPF
    if (dadosAtualizacao.cpf.length !== 11) {
        mostrarErro('mensagemErro', 'CPF deve ter exatamente 11 dígitos numéricos');
        return;
    }

    // Validar senha (se preenchida)
    if (dadosAtualizacao.senha && dadosAtualizacao.senha.length < 6) {
        mostrarErro('mensagemErro', 'Senha deve ter no mínimo 6 caracteres');
        return;
    }

    try {
        const response = await fetch(`/alunos/${alunoSelecionado.id}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(dadosAtualizacao)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.erro || 'Erro ao atualizar aluno');
        }

        mostrarSucesso('mensagemSucesso', data.mensagem || 'Aluno atualizado com sucesso');
        limparFormulario();
        carregarAlunos();

    } catch (erro) {
        mostrarErro('mensagemErro', erro.message);
    }
}

/**
 * Exclui o aluno selecionado
 */
async function excluirAluno() {
    if (!alunoSelecionado) {
        mostrarErro('mensagemErro', 'Selecione um aluno na tabela');
        return;
    }

    if (!confirm(`Tem certeza que deseja excluir o aluno "${alunoSelecionado.nome}"?`)) {
        return;
    }

    try {
        const response = await fetch(`/alunos/${alunoSelecionado.id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.erro || 'Erro ao excluir aluno');
        }

        mostrarSucesso('mensagemSucesso', data.mensagem || 'Aluno excluído com sucesso');
        limparFormulario();
        carregarAlunos();

    } catch (erro) {
        mostrarErro('mensagemErro', erro.message);
    }
}

/**
 * Altera o status da matrícula do aluno selecionado
 */
async function alterarStatus() {
    if (!alunoSelecionado) {
        mostrarErro('mensagemErro', 'Selecione um aluno na tabela');
        return;
    }

    // Alternar entre Ativo e Inativo
    const novoStatus = alunoSelecionado.status === 'Ativo' ? 'Inativo' : 'Ativo';

    try {
        const response = await fetch(`/alunos/${alunoSelecionado.id}/status`, {
            method: 'PATCH',
            headers: getAuthHeaders(),
            body: JSON.stringify({ status: novoStatus })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.erro || 'Erro ao alterar status');
        }

        mostrarSucesso('mensagemSucesso', data.mensagem || 'Status alterado com sucesso');
        limparFormulario();
        carregarAlunos();

    } catch (erro) {
        mostrarErro('mensagemErro', erro.message);
    }
}
