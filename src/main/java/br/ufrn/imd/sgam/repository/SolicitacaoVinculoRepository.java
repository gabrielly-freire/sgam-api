package br.ufrn.imd.sgam.repository;

import br.ufrn.imd.sgam.model.SolicitacaoVinculo;
import br.ufrn.imd.sgam.enums.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitacaoVinculoRepository extends JpaRepository<SolicitacaoVinculo, Long> {
    // Busca todas as solicitações de entrada que ainda não foram analisadas
    List<SolicitacaoVinculo> findAllByStatus(StatusSolicitacao status);
}