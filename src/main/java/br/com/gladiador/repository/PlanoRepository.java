package br.com.gladiador.repository;

import br.com.gladiador.model.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para operações com a tabela planos
 */
@Repository
public interface PlanoRepository extends JpaRepository<Plano, Integer> {

    Optional<Plano> findByNome(String nome);
}
