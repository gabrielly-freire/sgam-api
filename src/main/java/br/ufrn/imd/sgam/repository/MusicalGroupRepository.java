package br.ufrn.imd.sgam.repository;

import br.ufrn.imd.sgam.model.MusicalGroup;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicalGroupRepository extends GenericRepository<MusicalGroup> {

    boolean existsMusicalGroupByNome(String nome);

}