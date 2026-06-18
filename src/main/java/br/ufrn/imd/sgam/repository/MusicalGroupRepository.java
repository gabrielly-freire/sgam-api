package br.ufrn.imd.sgam.repository;

import br.ufrn.imd.sgam.model.MusicalGroup;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicalGroupRepository extends GenericRepository<MusicalGroup> {

    boolean existsMusicalGroupByNome(String nome);
    @Query("SELECT mg FROM MusicalGroup mg JOIN mg.integrantes i WHERE i.id = :alunoId")
    List<MusicalGroup> findAllByIntegrantesId(@Param("alunoId") Long alunoId);

}