package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.MusicalGroupDTO;
import br.ufrn.imd.sgam.exception.BusinessException;
import br.ufrn.imd.sgam.exception.ResourceNotFoundException;
import br.ufrn.imd.sgam.mapper.MusicalGroupMapper;
import br.ufrn.imd.sgam.model.MusicalGroup;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.repository.MusicalGroupRepository;
import br.ufrn.imd.sgam.repository.UserInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MusicalGroupServiceTest {

    @Mock
    private MusicalGroupRepository musicalGroupRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private MusicalGroupMapper musicalGroupMapper;

    @InjectMocks
    private MusicalGroupService musicalGroupService;

    // --- TESTES DO MÉTODO SAVE ---

    @Test
    @DisplayName("SAVE: Deve salvar grupo musical com sucesso")
    void testShouldSaveMusicalGroupSuccessfully() {
        MusicalGroupDTO dto = createDTO();
        MusicalGroup model = new MusicalGroup();
        UserInfo coordenador = new UserInfo();

        when(musicalGroupRepository.existsMusicalGroupByNome(dto.nome())).thenReturn(false);
        when(userInfoRepository.findById(dto.coordenadorId())).thenReturn(Optional.of(coordenador));
        when(musicalGroupMapper.toMusicalGroup(dto)).thenReturn(model);
        when(musicalGroupRepository.save(model)).thenReturn(model);
        when(musicalGroupMapper.toMusicalGroupDTO(model)).thenReturn(dto);

        MusicalGroupDTO result = musicalGroupService.save(dto);

        assertNotNull(result);
        verify(musicalGroupRepository).save(any());
    }

    @Test
    @DisplayName("SAVE: Deve falhar quando nome do grupo já existe")
    void testShouldFailSaveMusicalGroupWithExistingName() {
        MusicalGroupDTO dto = createDTO();
        when(musicalGroupRepository.existsMusicalGroupByNome(dto.nome())).thenReturn(true);

        assertThrows(BusinessException.class, () -> musicalGroupService.save(dto));
        verify(musicalGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("SAVE: Deve falhar quando coordenador não existe")
    void testShouldFailSaveMusicalGroupWithNonExistingCoordinator() {
        MusicalGroupDTO dto = createDTO();
        when(musicalGroupRepository.existsMusicalGroupByNome(dto.nome())).thenReturn(false);
        when(userInfoRepository.findById(dto.coordenadorId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> musicalGroupService.save(dto));
        verify(musicalGroupRepository, never()).save(any());
    }

    // --- TESTES DO MÉTODO GET ---

    @Test
    @DisplayName("GET: Deve retornar grupo por ID")
    void testShouldGetMusicalGroupById() {
        Long id = 1L;
        MusicalGroup model = new MusicalGroup();
        when(musicalGroupRepository.findById(id)).thenReturn(Optional.of(model));
        when(musicalGroupMapper.toMusicalGroupDTO(model)).thenReturn(createDTO());

        MusicalGroupDTO result = musicalGroupService.get(id);

        assertNotNull(result);
    }

    @Test
    @DisplayName("GET: Deve falhar ao buscar ID inexistente")
    void testShouldFailGetMusicalGroupById() {
        when(musicalGroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> musicalGroupService.get(1L));
    }

    // --- TESTES DO MÉTODO LIST (PAGINADO) ---

    @Test
    @DisplayName("LIST: Deve retornar página de grupos")
    void testShouldGetMusicalGroupsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        MusicalGroup model = new MusicalGroup();
        Page<MusicalGroup> page = new PageImpl<>(List.of(model));

        when(musicalGroupRepository.findAllPage(pageable)).thenReturn(page);
        when(musicalGroupMapper.toMusicalGroupDTO(model)).thenReturn(createDTO());

        Page<MusicalGroupDTO> result = musicalGroupService.list(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    // --- TESTES DO MÉTODO UPDATE ---

    @Test
    @DisplayName("UPDATE: Deve atualizar grupo com sucesso")
    void testShouldUpdateMusicalGroupSuccessfully() {
        Long id = 1L;
        MusicalGroupDTO dto = createDTO();
        MusicalGroup model = new MusicalGroup();
        UserInfo coordenador = new UserInfo();

        when(musicalGroupRepository.findById(id)).thenReturn(Optional.of(model));
        when(userInfoRepository.findById(dto.coordenadorId())).thenReturn(Optional.of(coordenador));
        when(musicalGroupMapper.toMusicalGroup(dto)).thenReturn(model);
        when(musicalGroupRepository.save(model)).thenReturn(model);
        when(musicalGroupMapper.toMusicalGroupDTO(model)).thenReturn(dto);

        MusicalGroupDTO result = musicalGroupService.update(id, dto);

        assertNotNull(result);
        verify(musicalGroupRepository).save(model);
    }

    @Test
    @DisplayName("UPDATE: Deve falhar ao atualizar ID inexistente")
    void testShouldFailUpdateMusicalGroupWithNonExistingId() {
        when(musicalGroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> musicalGroupService.update(1L, createDTO()));
    }

    // --- TESTES DO MÉTODO DELETE ---

    @Test
    @DisplayName("DELETE: Deve deletar com sucesso")
    void testShouldDeleteMusicalGroupSuccessfully() {
        Long id = 1L;
        when(musicalGroupRepository.findById(id)).thenReturn(Optional.of(new MusicalGroup()));

        musicalGroupService.delete(id);

        verify(musicalGroupRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("DELETE: Deve falhar ao deletar ID inexistente")
    void testShouldFailDeleteMusicalGroupWithNonExistingId() {
        when(musicalGroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> musicalGroupService.delete(1L));
    }

    private MusicalGroupDTO createDTO() {
        return new MusicalGroupDTO(1L, "Orquestra Sinfônica", 1L);
    }
}