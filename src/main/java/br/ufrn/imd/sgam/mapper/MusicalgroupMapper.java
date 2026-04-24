package br.ufrn.imd.sgam.mapper;

import br.ufrn.imd.sgam.dto.MusicalgroupDTO;
import br.ufrn.imd.sgam.model.Musicalgroup;
import org.springframework.stereotype.Component;

@Component
public class MusicalgroupMapper {

    public Musicalgroup toMusicalgroup(MusicalgroupDTO dto) {
        if (dto == null) {
            return null;
        }
        Musicalgroup musicalgroup = new Musicalgroup();
        musicalgroup.setId(dto.id());
        musicalgroup.setNome(dto.nome());
        return musicalgroup;
    }

    public MusicalgroupDTO toMusicalgroupDTO(Musicalgroup musicalgroup) {
        if (musicalgroup == null) {
            return null;
        }
        Long coordenadorId = null;
        if (musicalgroup.getCoordenador() != null) {
            coordenadorId = musicalgroup.getCoordenador().getId();
        }
        
        return new MusicalgroupDTO(
                musicalgroup.getId(),
                musicalgroup.getNome(),
                coordenadorId
        );
    }
}