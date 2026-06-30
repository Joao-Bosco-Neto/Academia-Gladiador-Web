-- BANCO DE DADOS — ACADEMIA GLADIADOR

CREATE DATABASE IF NOT EXISTS academia_db;
USE academia_db;

-- REMOVER TABELAS (ordem importa por FK)

DROP TABLE IF EXISTS matriculas;
DROP TABLE IF EXISTS alunos;
DROP TABLE IF EXISTS planos;
DROP TABLE IF EXISTS usuarios;

-- TABELA: usuarios
-- Responsável apenas pela autenticação

CREATE TABLE usuarios (

    id           INT AUTO_INCREMENT PRIMARY KEY,
    email        VARCHAR(100) NOT NULL UNIQUE,
    senha        VARCHAR(255) NOT NULL,
    tipo_usuario ENUM('admin', 'aluno') NOT NULL DEFAULT 'aluno',
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE INDEX idx_usuarios_email ON usuarios(email);

-- TABELA: planos

CREATE TABLE planos (

    id        INT AUTO_INCREMENT PRIMARY KEY,
    nome      ENUM('Anual', 'Mensal', 'Diária') NOT NULL UNIQUE,
    valor     DECIMAL(10,2) NOT NULL,
    descricao VARCHAR(255)

);

-- Inserir os planos disponíveis
INSERT INTO planos (nome, valor, descricao) VALUES
    ('Anual',  99.00,  'Plano anual com melhor custo-benefício'),
    ('Mensal', 129.00, 'Plano mensal sem fidelidade'),
    ('Diária', 20.00,  'Acesso avulso por dia');

-- TABELA: alunos
-- Dados pessoais e físicos do aluno

CREATE TABLE alunos (

    id                INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id        INT NOT NULL UNIQUE,

    nome              VARCHAR(100) NOT NULL,
    sexo              ENUM('Masculino', 'Feminino', 'Outro') NOT NULL,
    idade             INT NOT NULL,
    cpf               VARCHAR(14) NOT NULL UNIQUE,
    peso              DECIMAL(5,2) NOT NULL,
    altura            DECIMAL(4,2) NOT NULL,
    imc               DECIMAL(5,2),
    classificacao_imc VARCHAR(30),

    data_cadastro     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE,

    CHECK (idade > 0),
    CHECK (peso > 0),
    CHECK (altura > 0)

);

CREATE INDEX idx_alunos_nome      ON alunos(nome);
CREATE INDEX idx_alunos_cpf       ON alunos(cpf);
CREATE INDEX idx_alunos_usuario   ON alunos(usuario_id);

-- TABELA: matriculas
-- Vínculo entre aluno e plano
-- Permite histórico de trocas de plano

CREATE TABLE matriculas (

    id              INT AUTO_INCREMENT PRIMARY KEY,
    aluno_id        INT NOT NULL,
    plano_id        INT NOT NULL,
    status          ENUM('Ativo', 'Inativo') NOT NULL DEFAULT 'Ativo',
    data_inicio     DATE NOT NULL DEFAULT (CURRENT_DATE),
    data_vencimento DATE,

    FOREIGN KEY (aluno_id) REFERENCES alunos(id)
        ON DELETE CASCADE,
    FOREIGN KEY (plano_id) REFERENCES planos(id)

);

CREATE INDEX idx_matriculas_aluno  ON matriculas(aluno_id);
CREATE INDEX idx_matriculas_status ON matriculas(status);

-- TRIGGERS — ALUNOS

-- Calcular IMC no INSERT
DELIMITER $$
CREATE TRIGGER trg_calcular_imc_insert
BEFORE INSERT ON alunos
FOR EACH ROW
BEGIN
    SET NEW.imc = NEW.peso / (NEW.altura * NEW.altura);
END$$
DELIMITER ;

-- Calcular IMC no UPDATE
DELIMITER $$
CREATE TRIGGER trg_calcular_imc_update
BEFORE UPDATE ON alunos
FOR EACH ROW
BEGIN
    SET NEW.imc = NEW.peso / (NEW.altura * NEW.altura);
END$$
DELIMITER ;

-- Classificar IMC no INSERT
DELIMITER $$
CREATE TRIGGER trg_classificar_imc_insert
BEFORE INSERT ON alunos
FOR EACH ROW
BEGIN
    DECLARE v DECIMAL(5,2);
    SET v = NEW.peso / (NEW.altura * NEW.altura);
    IF    v < 18.5 THEN SET NEW.classificacao_imc = 'Abaixo do peso';
    ELSEIF v < 25  THEN SET NEW.classificacao_imc = 'Peso normal';
    ELSEIF v < 30  THEN SET NEW.classificacao_imc = 'Sobrepeso';
    ELSE                SET NEW.classificacao_imc = 'Obesidade';
    END IF;
END$$
DELIMITER ;

-- Classificar IMC no UPDATE
DELIMITER $$
CREATE TRIGGER trg_classificar_imc_update
BEFORE UPDATE ON alunos
FOR EACH ROW
BEGIN
    DECLARE v DECIMAL(5,2);
    SET v = NEW.peso / (NEW.altura * NEW.altura);
    IF    v < 18.5 THEN SET NEW.classificacao_imc = 'Abaixo do peso';
    ELSEIF v < 25  THEN SET NEW.classificacao_imc = 'Peso normal';
    ELSEIF v < 30  THEN SET NEW.classificacao_imc = 'Sobrepeso';
    ELSE                SET NEW.classificacao_imc = 'Obesidade';
    END IF;
END$$
DELIMITER ;

-- Validar CPF no INSERT
DELIMITER $$
CREATE TRIGGER trg_validar_cpf
BEFORE INSERT ON alunos
FOR EACH ROW
BEGIN
    IF LENGTH(NEW.cpf) < 14 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'CPF inválido. Use o formato XXX.XXX.XXX-XX';
    END IF;
END$$
DELIMITER ;

-- TRIGGER — MATRICULAS
-- Calcular data de vencimento automaticamente

DELIMITER $$
CREATE TRIGGER trg_vencimento_matricula
BEFORE INSERT ON matriculas
FOR EACH ROW
BEGIN
    DECLARE nome_plano ENUM('Anual', 'Mensal', 'Diária');
    SELECT nome INTO nome_plano FROM planos WHERE id = NEW.plano_id;

    CASE nome_plano
        WHEN 'Anual'   THEN SET NEW.data_vencimento = DATE_ADD(NEW.data_inicio, INTERVAL 1 YEAR);
        WHEN 'Mensal'  THEN SET NEW.data_vencimento = DATE_ADD(NEW.data_inicio, INTERVAL 1 MONTH);
        WHEN 'Diária'  THEN SET NEW.data_vencimento = DATE_ADD(NEW.data_inicio, INTERVAL 1 DAY);
    END CASE;
END$$
DELIMITER ;

-- INSERIR ADMINISTRADOR PADRÃO
-- Senha padrão: 123456 (hash BCrypt gerado com 10 rounds)

INSERT INTO usuarios (email, senha, tipo_usuario)
VALUES ('admin@academia.com', '$2b$10$diWtiYr8bLKfPABgMOGy8ezpzxZCr/x2Jb85lHxlRJl0qUKEloSTO', 'admin');

INSERT INTO alunos (usuario_id, nome, sexo, idade, cpf, peso, altura)
VALUES (
    LAST_INSERT_ID(),
    'Administrador',
    'Masculino',
    30,
    '000.000.000-00',
    80.00,
    1.80
);

-- Matrícula do administrador (necessária para evitar NullPointerException na view)
INSERT INTO matriculas (aluno_id, plano_id, status)
VALUES (
    (SELECT id FROM alunos WHERE cpf = '000.000.000-00'),
    (SELECT id FROM planos WHERE nome = 'Anual'),
    'Ativo'
);

-- VIEW — para o Java consultar tudo de uma vez

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
    p.nome       AS plano,
    p.valor      AS mensalidade,
    m.status,
    m.data_inicio,
    m.data_vencimento,
    a.data_cadastro
FROM alunos a
JOIN usuarios   u ON u.id = a.usuario_id
LEFT JOIN matriculas m ON m.aluno_id = a.id
    AND m.status = 'Ativo'
LEFT JOIN planos p ON p.id = m.plano_id;

-- CONSULTAS ÚTEIS

-- Listar todos os alunos com plano e status
SELECT * FROM vw_alunos;

-- Listar somente ativos
SELECT * FROM vw_alunos WHERE status = 'Ativo';

-- Pesquisar por nome
SELECT * FROM vw_alunos WHERE nome LIKE '%João%';

-- Login
SELECT * FROM vw_alunos
WHERE email = 'admin@academia.com'
AND senha = '123456';

-- Histórico de planos de um aluno
SELECT a.nome, p.nome AS plano, m.status, m.data_inicio, m.data_vencimento
FROM matriculas m
JOIN alunos a ON a.id = m.aluno_id
JOIN planos p ON p.id = m.plano_id
WHERE a.id = 1;
