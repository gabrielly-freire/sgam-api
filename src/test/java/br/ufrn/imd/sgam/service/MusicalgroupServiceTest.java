package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.MusicalgroupDTO;
import br.ufrn.imd.sgam.exception.BusinessException;
import br.ufrn.imd.sgam.exception.ResourceNotFoundException;
import br.ufrn.imd.sgam.mapper.MusicalgroupMapper;
import br.ufrn.imd.sgam.model.Musicalgroup;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.repository.MusicalgroupRepository;
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
public class MusicalgroupServiceTest {

    @Mock
    private MusicalgroupRepository musicalgroupRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private MusicalgroupMapper musicalgroupMapper;

    @InjectMocks
    private MusicalgroupService musicalgroupService;

    // --- TESTES DO MÉTODO SAVE ---

    @Test
    @DisplayName("SAVE: Deve salvar grupo musical com sucesso")
    void testShouldSaveMusicalgroupSuccessfully() {
        MusicalgroupDTO dto = createDTO();
        Musicalgroup model = new Musicalgroup();
        UserInfo coordenador = new UserInfo();

        when(musicalgroupRepository.existsMusicalgroupByNome(dto.nome())).thenReturn(false);
        when(userInfoRepository.findById(dto.coordenadorId())).thenReturn(Optional.of(coordenador));
        when(musicalgroupMapper.toMusicalgroup(dto)).thenReturn(model);
        when(musicalgroupRepository.save(model)).thenReturn(model);
        when(musicalgroupMapper.toMusicalgroupDTO(model)).thenReturn(dto);

        MusicalgroupDTO result = musicalgroupService.save(dto);

        assertNotNull(result);
        verify(musicalgroupRepository).save(any());
    }

    @Test
    @DisplayName("SAVE: Deve falhar quando nome do grupo já existe")
    void testShouldFailSaveMusicalgroupWithExistingName() {
        MusicalgroupDTO dto = createDTO();
        when(musicalgroupRepository.existsMusicalgroupByNome(dto.nome())).thenReturn(true);

        assertThrows(BusinessException.class, () -> musicalgroupService.save(dto));
        verify(musicalgroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("SAVE: Deve falhar quando coordenador não existe")
    void testShouldFailSaveMusicalgroupWithNonExistingCoordinator() {
        MusicalgroupDTO dto = createDTO();
        when(musicalgroupRepository.existsMusicalgroupByNome(dto.nome())).thenReturn(false);
        when(userInfoRepository.findById(dto.coordenadorId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> musicalgroupService.save(dto));
        verify(musicalgroupRepository, never()).save(any());
    }

    // --- TESTES DO MÉTODO GET ---

    @Test
    @DisplayName("GET: Deve retornar grupo por ID")
    void testShouldGetMusicalgroupById() {
        Long id = 1L;
        Musicalgroup model = new Musicalgroup();
        when(musicalgroupRepository.findById(id)).thenReturn(Optional.of(model));
        when(musicalgroupMapper.toMusicalgroupDTO(model)).thenReturn(createDTO());

        MusicalgroupDTO result = musicalgroupService.get(id);

        assertNotNull(result);
    }

    @Test
    @DisplayName("GET: Deve falhar ao buscar ID inexistente")
    void testShouldFailGetMusicalgroupById() {
        when(musicalgroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> musicalgroupService.get(1L));
    }

    // --- TESTES DO MÉTODO LIST (PAGINADO) ---

    @Test
    @DisplayName("LIST: Deve retornar página de grupos")
    void testShouldGetMusicalgroupsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Musicalgroup model = new Musicalgroup();
        Page<Musicalgroup> page = new PageImpl<>(List.of(model));

        when(musicalgroupRepository.findAllPage(pageable)).thenReturn(page);
        when(musicalgroupMapper.toMusicalgroupDTO(model)).thenReturn(createDTO());

        Page<MusicalgroupDTO> result = musicalgroupService.list(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    // --- TESTES DO MÉTODO UPDATE ---

    @Test
    @DisplayName("UPDATE: Deve atualizar grupo com sucesso")
    void testShouldUpdateMusicalgroupSuccessfully() {
        Long id = 1L;
        MusicalgroupDTO dto = createDTO();
        Musicalgroup model = new Musicalgroup();
        UserInfo coordenador = new UserInfo();

        when(musicalgroupRepository.findById(id)).thenReturn(Optional.of(model));
        when(userInfoRepository.findById(dto.coordenadorId())).thenReturn(Optional.of(coordenador));
        when(musicalgroupMapper.toMusicalgroup(dto)).thenReturn(model);
        when(musicalgroupRepository.save(model)).thenReturn(model);
        when(musicalgroupMapper.toMusicalgroupDTO(model)).thenReturn(dto);

        MusicalgroupDTO result = musicalgroupService.update(id, dto);

        assertNotNull(result);
        verify(musicalgroupRepository).save(model);
    }

    @Test
    @DisplayName("UPDATE: Deve falhar ao atualizar ID inexistente")
    void testShouldFailUpdateMusicalgroupWithNonExistingId() {
        when(musicalgroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> musicalgroupService.update(1L, createDTO()));
    }

    // --- TESTES DO MÉTODO DELETE ---

    @Test
    @DisplayName("DELETE: Deve deletar com sucesso")
    void testShouldDeleteMusicalgroupSuccessfully() {
        Long id = 1L;
        when(musicalgroupRepository.findById(id)).thenReturn(Optional.of(new Musicalgroup()));

        musicalgroupService.delete(id);

        verify(musicalgroupRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("DELETE: Deve falhar ao deletar ID inexistente")
    void testShouldFailDeleteMusicalgroupWithNonExistingId() {
        when(musicalgroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> musicalgroupService.delete(1L));
    }

    private MusicalgroupDTO createDTO() {
        return new MusicalgroupDTO(1L, "Orquestra Sinfônica", 1L);
    }
}