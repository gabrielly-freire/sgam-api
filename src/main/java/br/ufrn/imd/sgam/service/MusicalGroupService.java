package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.MusicalGroupDTO;
import br.ufrn.imd.sgam.dto.SolicitacaoVinculoDTO;
import br.ufrn.imd.sgam.enums.StatusSolicitacao;
import br.ufrn.imd.sgam.exception.BusinessException;
import br.ufrn.imd.sgam.exception.ResourceNotFoundException;
import br.ufrn.imd.sgam.mapper.MusicalGroupMapper;
import br.ufrn.imd.sgam.model.MusicalGroup;
import br.ufrn.imd.sgam.model.SolicitacaoVinculo;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.repository.MusicalGroupRepository;
import br.ufrn.imd.sgam.repository.SolicitacaoVinculoRepository;
import br.ufrn.imd.sgam.repository.UserInfoRepository;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MusicalGroupService {

    private final MusicalGroupRepository musicalGroupRepository;
    private final UserInfoRepository userInfoRepository;
    private final MusicalGroupMapper musicalGroupMapper;
    private final SolicitacaoVinculoRepository solicitacaoVinculoRepository;

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

    @Transactional // Garante contexto transacional na criação
    public void solicitarVinculo(Long grupoId) {
        MusicalGroup grupo = musicalGroupRepository.findById(grupoId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo musical não encontrado"));

        Object principal = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String username;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }

        UserInfo alunoLogado = userInfoRepository.findByUsername(username);

        if (alunoLogado == null) {
            throw new ResourceNotFoundException("Usuário logado não encontrado no sistema.");
        }

        SolicitacaoVinculo solicitacao = new SolicitacaoVinculo();
        solicitacao.setAluno(alunoLogado);
        solicitacao.setMusicalGroup(grupo);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);

        solicitacaoVinculoRepository.save(solicitacao);
    }

    public List<SolicitacaoVinculoDTO> listarSolicitacoesPendentes() {
        return solicitacaoVinculoRepository.findAllByStatus(StatusSolicitacao.PENDENTE)
                .stream()
                .map(SolicitacaoVinculoDTO::new)
                .toList();
    }

    @Transactional
    public void aprovarSolicitacao(Long solicitacaoId) {
        SolicitacaoVinculo solicitacao = solicitacaoVinculoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new BusinessException("Esta solicitação já foi processada.", HttpStatus.BAD_REQUEST);
        }

        solicitacaoVinculoRepository.atualizarStatus(solicitacaoId, StatusSolicitacao.APROVADO);
    }

    @Transactional
    public void recusarSolicitacao(Long solicitacaoId) {
        SolicitacaoVinculo solicitacao = solicitacaoVinculoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new BusinessException("Esta solicitação já foi processada.", HttpStatus.BAD_REQUEST);
        }

        solicitacaoVinculoRepository.atualizarStatus(solicitacaoId, StatusSolicitacao.RECUSADO);
    }
}