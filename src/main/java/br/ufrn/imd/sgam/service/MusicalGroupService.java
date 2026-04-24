package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.MusicalGroupDTO;
import br.ufrn.imd.sgam.exception.BusinessException;
import br.ufrn.imd.sgam.exception.ResourceNotFoundException;
import br.ufrn.imd.sgam.mapper.MusicalGroupMapper;
import br.ufrn.imd.sgam.model.MusicalGroup;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.repository.MusicalGroupRepository;
import br.ufrn.imd.sgam.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MusicalGroupService {

    private final MusicalGroupRepository musicalgroupRepository;
    private final UserInfoRepository userInfoRepository;
    private final MusicalGroupMapper musicalgroupMapper;

    public MusicalGroupDTO save(MusicalGroupDTO dto) {
        if (musicalgroupRepository.existsMusicalGroupByNome(dto.nome())) {
            throw new BusinessException("Já existe um grupo musical com este nome.", HttpStatus.CONFLICT);
        }

        UserInfo coordenador = userInfoRepository.findById(dto.coordenadorId()).orElseThrow(
                () -> new ResourceNotFoundException("Coordenador não encontrado"));

        MusicalGroup musicalgroup = musicalgroupMapper.toMusicalGroup(dto);
        musicalgroup.setCoordenador(coordenador);
        
        musicalgroup = musicalgroupRepository.save(musicalgroup);
        return musicalgroupMapper.toMusicalGroupDTO(musicalgroup);
    }

    public MusicalGroupDTO get(Long id) {
        MusicalGroup musicalgroup = musicalgroupRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Grupo musical não encontrado"));
        return musicalgroupMapper.toMusicalGroupDTO(musicalgroup);
    }

    public Page<MusicalGroupDTO> list(Pageable pageable) {
        Page<MusicalGroup> musicalgroups = musicalgroupRepository.findAllPage(pageable);
        return musicalgroups.map(musicalgroupMapper::toMusicalGroupDTO);
    }

    public MusicalGroupDTO update(Long id, MusicalGroupDTO dto) {
        musicalgroupRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Grupo musical não encontrado"));

        UserInfo coordenador = userInfoRepository.findById(dto.coordenadorId()).orElseThrow(
                () -> new ResourceNotFoundException("Coordenador não encontrado"));

        MusicalGroup musicalgroup = musicalgroupMapper.toMusicalGroup(dto);
        musicalgroup.setId(id);
        musicalgroup.setCoordenador(coordenador);
        
        musicalgroup = musicalgroupRepository.save(musicalgroup);
        return musicalgroupMapper.toMusicalGroupDTO(musicalgroup);
    }

    public void delete(Long id) {
        musicalgroupRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Grupo musical não encontrado"));
        //TODO: regras de exclusão

        musicalgroupRepository.deleteById(id);
    }

}