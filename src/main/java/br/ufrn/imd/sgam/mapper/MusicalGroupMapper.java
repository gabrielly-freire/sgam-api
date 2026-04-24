package br.ufrn.imd.sgam.mapper;

import br.ufrn.imd.sgam.dto.MusicalGroupDTO;
import br.ufrn.imd.sgam.model.MusicalGroup;
import org.springframework.stereotype.Component;

@Component
public class MusicalGroupMapper {

    public MusicalGroup toMusicalGroup(MusicalGroupDTO dto) {
        if (dto == null) {
            return null;
        }
        MusicalGroup musicalgroup = new MusicalGroup();
        musicalgroup.setId(dto.id());
        musicalgroup.setNome(dto.nome());
        return musicalgroup;
    }

    public MusicalGroupDTO toMusicalGroupDTO(MusicalGroup musicalgroup) {
        if (musicalgroup == null) {
            return null;
        }
        Long coordenadorId = null;
        if (musicalgroup.getCoordenador() != null) {
            coordenadorId = musicalgroup.getCoordenador().getId();
        }
        
        return new MusicalGroupDTO(
                musicalgroup.getId(),
                musicalgroup.getNome(),
                coordenadorId
        );
    }
}