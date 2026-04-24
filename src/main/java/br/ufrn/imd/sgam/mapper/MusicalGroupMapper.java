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
        MusicalGroup musicalGroup = new MusicalGroup();
        musicalGroup.setId(dto.id());
        musicalGroup.setNome(dto.nome());
        return musicalGroup;
    }

    public MusicalGroupDTO toMusicalGroupDTO(MusicalGroup musicalGroup) {
        if (musicalGroup == null) {
            return null;
        }
        Long coordenadorId = null;
        if (musicalGroup.getCoordenador() != null) {
            coordenadorId = musicalGroup.getCoordenador().getId();
        }
        
        return new MusicalGroupDTO(
                musicalGroup.getId(),
                musicalGroup.getNome(),
                coordenadorId
        );
    }
}