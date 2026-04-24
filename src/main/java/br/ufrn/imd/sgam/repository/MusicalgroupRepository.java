package br.ufrn.imd.sgam.repository;

import br.ufrn.imd.sgam.model.Musicalgroup;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicalgroupRepository extends GenericRepository<Musicalgroup> {

    boolean existsMusicalgroupByNome(String nome);

}