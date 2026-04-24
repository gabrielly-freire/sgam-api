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

    private final MusicalGroupRepository musicalGroupRepository;
    private final UserInfoRepository userInfoRepository;
    private final MusicalGroupMapper musicalGroupMapper;

    public MusicalGroupDTO save(MusicalGroupDTO dto) {
        if (musicalGroupRepository.existsMusicalGroupByNome(dto.nome())) {
            throw new BusinessException("Já existe um grupo musical com este nome.", HttpStatus.CONFLICT);
        }

        UserInfo coordenador = userInfoRepository.findById(dto.coordenadorId()).orElseThrow(
                () -> new ResourceNotFoundException("Coordenador não encontrado"));

        MusicalGroup musicalGroup = musicalGroupMapper.toMusicalGroup(dto);
        musicalGroup.setCoordenador(coordenador);
        
        musicalGroup = musicalGroupRepository.save(musicalGroup);
        return musicalGroupMapper.toMusicalGroupDTO(musicalGroup);
    }

    public MusicalGroupDTO get(Long id) {
        MusicalGroup musicalGroup = musicalGroupRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Grupo musical não encontrado"));
        return musicalGroupMapper.toMusicalGroupDTO(musicalGroup);
    }

    public Page<MusicalGroupDTO> list(Pageable pageable) {
        Page<MusicalGroup> musicalGroups = musicalGroupRepository.findAllPage(pageable);
        return musicalGroups.map(musicalGroupMapper::toMusicalGroupDTO);
    }

    public MusicalGroupDTO update(Long id, MusicalGroupDTO dto) {
        musicalGroupRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Grupo musical não encontrado"));

        UserInfo coordenador = userInfoRepository.findById(dto.coordenadorId()).orElseThrow(
                () -> new ResourceNotFoundException("Coordenador não encontrado"));

        MusicalGroup musicalGroup = musicalGroupMapper.toMusicalGroup(dto);
        musicalGroup.setId(id);
        musicalGroup.setCoordenador(coordenador);
        
        musicalGroup = musicalGroupRepository.save(musicalGroup);
        return musicalGroupMapper.toMusicalGroupDTO(musicalGroup);
    }

    public void delete(Long id) {
        musicalGroupRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Grupo musical não encontrado"));
        //TODO: regras de exclusão

        musicalGroupRepository.deleteById(id);
    }

}