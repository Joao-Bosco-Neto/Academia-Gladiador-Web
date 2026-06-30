/**
 * ACADEMIA GLADIADOR - Cadastro de Alunos
 */

// Handler do formulário de cadastro
document.getElementById('formCadastro').addEventListener('submit', async (e) => {
    e.preventDefault();

    // Coletar dados do formulário
    const dadosCadastro = {
        nome: document.getElementById('nome').value.trim(),
        sexo: document.getElementById('sexo').value,
        idade: parseInt(document.getElementById('idade').value),
        peso: parseFloat(document.getElementById('peso').value),
        altura: parseFloat(document.getElementById('altura').value),
        email: document.getElementById('email').value.trim(),
        cpf: limparCPF(document.getElementById('cpf').value), // Remove qualquer formatação
        senha: document.getElementById('senha').value,
        plano: document.getElementById('plano').value
    };

    // Validar CPF (exatamente 11 dígitos)
    if (dadosCadastro.cpf.length !== 11) {
        mostrarErro('mensagemErro', 'CPF deve ter exatamente 11 dígitos numéricos');
        return;
    }

    // Validar senha (mínimo 6 caracteres)
    if (dadosCadastro.senha.length < 6) {
        mostrarErro('mensagemErro', 'Senha deve ter no mínimo 6 caracteres');
        return;
    }

    try {
        const resultado = await cadastrar(dadosCadastro);

        // Mostra mensagem de sucesso (backend retorna na chave "mensagem")
        mostrarSucesso('mensagemSucesso', resultado.mensagem || 'Cadastro realizado com sucesso');

        // Limpa o formulário
        document.getElementById('formCadastro').reset();

        // Redireciona para login após 2 segundos
        setTimeout(() => {
            window.location.href = '/index.html';
        }, 2000);
    } catch (erro) {
        mostrarErro('mensagemErro', erro.message);
    }
});

// Aceitar apenas números no campo CPF
document.getElementById('cpf').addEventListener('input', (e) => {
    e.target.value = e.target.value.replace(/\D/g, '');
});
