package br.com.gladiador.repository;

import br.com.gladiador.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para operações com a tabela matriculas
 */
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {

    Optional<Matricula> findByAlunoId(Integer alunoId);
}
