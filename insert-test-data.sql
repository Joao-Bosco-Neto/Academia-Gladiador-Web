-- Script para inserir dados de teste no banco Academia Gladiador
-- Execute este script após criar as tabelas

USE academia_db;

-- Limpar dados existentes (cuidado em produção!)
-- DELETE FROM matriculas;
-- DELETE FROM alunos;
-- DELETE FROM usuarios WHERE tipo_usuario = 'aluno';
-- DELETE FROM planos;

-- Inserir planos (se ainda não existirem)
INSERT IGNORE INTO planos (id, nome, valor, descricao) VALUES
(1, 'Anual', 99.00, 'Plano anual com 12 meses de acesso completo à academia'),
(2, 'Mensal', 129.00, 'Plano mensal com renovação automática'),
(3, 'Diária', 20.00, 'Acesso diário à academia, ideal para treinos esporádicos');

-- Inserir usuário admin (senha: admin123)
-- Hash BCrypt de "admin123": $2a$10$8K1p/a0dL3.XR4a6/fIrseK9Z8KlrjxfK9FxzVzKJ5/K3L0/K2JZG
INSERT IGNORE INTO usuarios (id, email, senha, tipo_usuario, data_cadastro) VALUES
(1, 'admin@gladiador.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin', NOW());

-- Inserir alunos de teste (senha: senha123 para todos)
-- Hash BCrypt de "senha123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO usuarios (email, senha, tipo_usuario, data_cadastro) VALUES
('carlos@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'aluno', NOW()),
('ana@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'aluno', NOW()),
('pedro@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'aluno', NOW());

-- Inserir dados dos alunos
INSERT INTO alunos (usuario_id, nome, sexo, idade, cpf, peso, altura, imc, classificacao_imc, data_cadastro, ultima_atualizacao) VALUES
(2, 'Carlos Mendes', 'M', 30, '11111111111', 85.0, 1.80, 26.23, 'Sobrepeso', NOW(), NOW()),
(3, 'Ana Paula', 'F', 25, '22222222222', 60.0, 1.65, 22.04, 'Peso normal', NOW(), NOW()),
(4, 'Pedro Santos', 'M', 35, '33333333333', 95.0, 1.75, 31.02, 'Obesidade', NOW(), NOW());

-- Inserir matrículas
INSERT INTO matriculas (aluno_id, plano_id, status, data_inicio, data_vencimento) VALUES
(1, 2, 'Ativo', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH)),  -- Carlos - Mensal
(2, 1, 'Ativo', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR)),   -- Ana - Anual
(3, 3, 'Inativo', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY));  -- Pedro - Diária (Inativo)

-- Verificar dados inseridos
SELECT 'Usuários inseridos:' AS Info;
SELECT * FROM usuarios;

SELECT 'Planos inseridos:' AS Info;
SELECT * FROM planos;

SELECT 'Alunos inseridos:' AS Info;
SELECT * FROM alunos;

SELECT 'Matrículas inseridas:' AS Info;
SELECT * FROM matriculas;

SELECT 'View vw_alunos:' AS Info;
SELECT * FROM vw_alunos;
