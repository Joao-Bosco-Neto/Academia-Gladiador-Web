package br.com.gladiador.repository;

import br.com.gladiador.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações com a tabela alunos
 */
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {

    boolean existsByCpf(String cpf);

    @Query("SELECT a FROM Aluno a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR a.cpf LIKE CONCAT('%', :termo, '%')")
    List<Aluno> buscarPorNomeOuCpf(@Param("termo") String termo);
}
