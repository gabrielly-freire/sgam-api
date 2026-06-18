package br.ufrn.imd.sgam.repository;

import br.ufrn.imd.sgam.model.SolicitacaoVinculo;
import br.ufrn.imd.sgam.enums.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitacaoVinculoRepository extends JpaRepository<SolicitacaoVinculo, Long> {
    
    List<SolicitacaoVinculo> findAllByStatus(StatusSolicitacao status);

    @Modifying
    @Query("UPDATE SolicitacaoVinculo s SET s.status = :status WHERE s.id = :id")
    void atualizarStatus(@Param("id") Long id, @Param("status") StatusSolicitacao status);
}