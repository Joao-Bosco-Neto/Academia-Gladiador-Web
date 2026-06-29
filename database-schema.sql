-- Script SQL para criação do banco de dados Academia Gladiador
-- Este script é apenas para referência, o banco já deve existir

CREATE DATABASE IF NOT EXISTS academia_db;
USE academia_db;

-- Tabela de usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(10) NOT NULL,
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de planos
CREATE TABLE IF NOT EXISTS planos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    descricao TEXT
);

-- Inserir planos padrão
INSERT INTO planos (nome, valor, descricao) VALUES
('Anual', 99.00, 'Plano anual com 12 meses de acesso'),
('Mensal', 129.00, 'Plano mensal com renovação automática'),
('Diária', 20.00, 'Acesso diário à academia');

-- Tabela de alunos
CREATE TABLE IF NOT EXISTS alunos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    sexo CHAR(1) NOT NULL,
    idade INT NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    peso DECIMAL(5, 2) NOT NULL,
    altura DECIMAL(3, 2) NOT NULL,
    imc DECIMAL(5, 2) NOT NULL,
    classificacao_imc VARCHAR(50) NOT NULL,
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
    ultima_atualizacao DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabela de matrículas
CREATE TABLE IF NOT EXISTS matriculas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    aluno_id INT NOT NULL,
    plano_id INT NOT NULL,
    status VARCHAR(10) NOT NULL,
    data_inicio DATE,
    data_vencimento DATE,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    FOREIGN KEY (plano_id) REFERENCES planos(id)
);

-- View para consolidar dados dos alunos
CREATE OR REPLACE VIEW vw_alunos AS
SELECT
    a.id,
    a.nome,
    a.sexo,
    a.idade,
    a.cpf,
    a.peso,
    a.altura,
    a.imc,
    a.classificacao_imc,
    u.email,
    u.senha,
    u.tipo_usuario,
    p.nome AS plano,
    p.valor AS mensalidade,
    m.status,
    m.data_inicio,
    m.data_vencimento,
    a.data_cadastro
FROM alunos a
INNER JOIN usuarios u ON a.usuario_id = u.id
LEFT JOIN matriculas m ON a.id = m.aluno_id
LEFT JOIN planos p ON m.plano_id = p.id;

-- Criar usuário admin padrão (senha: admin123)
-- Senha criptografada com BCrypt
INSERT INTO usuarios (email, senha, tipo_usuario) VALUES
('admin@gladiador.com', '$2a$10$8K1p/a0dL3.XR4a6/fIrseK9Z8KlrjxfK9FxzVzKJ5/K3L0/K2JZG', 'admin');
