package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.MusicalgroupDTO;
import br.ufrn.imd.sgam.exception.BusinessException;
import br.ufrn.imd.sgam.exception.ResourceNotFoundException;
import br.ufrn.imd.sgam.mapper.MusicalgroupMapper;
import br.ufrn.imd.sgam.model.Musicalgroup;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.repository.MusicalgroupRepository;
import br.ufrn.imd.sgam.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MusicalgroupService {

    private final MusicalgroupRepository musicalgroupRepository;
    private final UserInfoRepository userInfoRepository;
    private final MusicalgroupMapper musicalgroupMapper;

    public MusicalgroupDTO save(MusicalgroupDTO dto) {
        if (musicalgroupRepository.existsMusicalgroupByNome(dto.nome())) {
            throw new BusinessException("Já existe um grupo musical com este nome.", HttpStatus.CONFLICT);
        }

        UserInfo coordenador = userInfoRepository.findById(dto.coordenadorId()).orElseThrow(
                () -> new ResourceNotFoundException("Coordenador não encontrado"));

        Musicalgroup musicalgroup = musicalgroupMapper.toMusicalgroup(dto);
        musicalgroup.setCoordenador(coordenador);
        
        musicalgroup = musicalgroupRepository.save(musicalgroup);
        return musicalgroupMapper.toMusicalgroupDTO(musicalgroup);
    }

    public MusicalgroupDTO get(Long id) {
        Musicalgroup musicalgroup = musicalgroupRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Grupo musical não encontrado"));
        return musicalgroupMapper.toMusicalgroupDTO(musicalgroup);
    }

    public Page<MusicalgroupDTO> list(Pageable pageable) {
        Page<Musicalgroup> musicalgroups = musicalgroupRepository.findAllPage(pageable);
        return musicalgroups.map(musicalgroupMapper::toMusicalgroupDTO);
    }

    public MusicalgroupDTO update(Long id, MusicalgroupDTO dto) {
        musicalgroupRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Grupo musical não encontrado"));

        UserInfo coordenador = userInfoRepository.findById(dto.coordenadorId()).orElseThrow(
                () -> new ResourceNotFoundException("Coordenador não encontrado"));

        Musicalgroup musicalgroup = musicalgroupMapper.toMusicalgroup(dto);
        musicalgroup.setId(id);
        musicalgroup.setCoordenador(coordenador);
        
        musicalgroup = musicalgroupRepository.save(musicalgroup);
        return musicalgroupMapper.toMusicalgroupDTO(musicalgroup);
    }

    public void delete(Long id) {
        musicalgroupRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Grupo musical não encontrado"));
        //TODO: regras de exclusão

        musicalgroupRepository.deleteById(id);
    }

}