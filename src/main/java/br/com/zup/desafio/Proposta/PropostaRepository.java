package br.com.zup.desafio.Proposta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropostaRepository extends JpaRepository<Proposta, Long>{


	boolean existsByDocumento(String documento);

}
