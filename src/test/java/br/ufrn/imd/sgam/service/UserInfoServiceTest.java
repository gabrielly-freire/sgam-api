package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.UserInfoDTO;
import br.ufrn.imd.sgam.enums.Role;
import br.ufrn.imd.sgam.exception.BusinessException;
import br.ufrn.imd.sgam.exception.ResourceNotFoundException;
import br.ufrn.imd.sgam.mapper.UserInfoMapper;
import br.ufrn.imd.sgam.model.UserInfo;
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
public class UserInfoServiceTest {

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private UserInfoMapper userInfoMapper;

    @InjectMocks
    private UserInfoService userInfoService;

    // --- TESTES DO MÉTODO SAVE ---

    @Test
    @DisplayName("SAVE: Deve salvar usuário com sucesso")
    void testShouldSaveUserSuccessfully () {
        UserInfoDTO dto = createDTO();
        UserInfo model = new UserInfo();

        when(userInfoRepository.existsUserInfoByEmail(dto.email())).thenReturn(false);
        when(userInfoRepository.existsUserInfoByUsername(dto.username())).thenReturn(false);
        when(userInfoMapper.toUserInfo(dto)).thenReturn(model);
        when(userInfoRepository.save(model)).thenReturn(model);
        when(userInfoMapper.toUserInfoDTO(model)).thenReturn(dto);

        UserInfoDTO result = userInfoService.save(dto);

        assertNotNull(result);
        verify(userInfoRepository).save(any());
    }

    @Test
    @DisplayName("SAVE: Deve falhar quando email já existe")
    void testShouldFailSaveUserWithExistingEmail() {
        UserInfoDTO dto = createDTO();
        when(userInfoRepository.existsUserInfoByEmail(dto.email())).thenReturn(true);

        assertThrows(BusinessException.class, () -> userInfoService.save(dto));
        verify(userInfoRepository, never()).save(any());
    }

    @Test
    @DisplayName("SAVE: Deve falhar quando username já existe")
    void testShouldFailSaveUserWithExistingUsername() {
        UserInfoDTO dto = createDTO();
        when(userInfoRepository.existsUserInfoByEmail(dto.email())).thenReturn(false);
        when(userInfoRepository.existsUserInfoByUsername(dto.username())).thenReturn(true);

        assertThrows(BusinessException.class, () -> userInfoService.save(dto));
        verify(userInfoRepository, never()).save(any());
    }

    // --- TESTES DO MÉTODO GET ---

    @Test
    @DisplayName("GET: Deve retornar usuário por ID")
    void testShouldGetUserById() {
        Long id = 1L;
        UserInfo model = new UserInfo();
        when(userInfoRepository.findById(id)).thenReturn(Optional.of(model));
        when(userInfoMapper.toUserInfoDTO(model)).thenReturn(createDTO());

        UserInfoDTO result = userInfoService.get(id);

        assertNotNull(result);
    }

    @Test
    @DisplayName("GET: Deve falhar ao buscar ID inexistente")
    void testShouldFailGetUserById() {
        when(userInfoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userInfoService.get(1L));
    }

    // --- TESTES DO MÉTODO LIST (PAGINADO) ---

    @Test
    @DisplayName("LIST: Deve retornar página de usuários")
    void testShouldGetUsersPage() {
        Pageable pageable = PageRequest.of(0, 10);
        UserInfo model = new UserInfo();
        Page<UserInfo> page = new PageImpl<>(List.of(model));

        when(userInfoRepository.findAllPage(pageable)).thenReturn(page);
        when(userInfoMapper.toUserInfoDTO(model)).thenReturn(createDTO());

        Page<UserInfoDTO> result = userInfoService.list(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    // --- TESTES DO MÉTODO UPDATE ---

    @Test
    @DisplayName("UPDATE: Deve atualizar usuário com sucesso")
    void testShouldUpdateUserSuccessfully() {
        Long id = 1L;
        UserInfoDTO dto = createDTO();
        UserInfo model = new UserInfo();

        when(userInfoRepository.findById(id)).thenReturn(Optional.of(model));
        when(userInfoMapper.toUserInfo(dto)).thenReturn(model);
        when(userInfoRepository.save(model)).thenReturn(model);
        when(userInfoMapper.toUserInfoDTO(model)).thenReturn(dto);

        UserInfoDTO result = userInfoService.update(id, dto);

        assertNotNull(result);
        verify(userInfoRepository).save(model);
    }

    @Test
    @DisplayName("UPDATE: Deve falhar ao atualizar ID inexistente")
    void testShouldFailUpdateUserWithNonExistingId() {
        when(userInfoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userInfoService.update(1L, createDTO()));
    }

    // --- TESTES DO MÉTODO DELETE ---

    @Test
    @DisplayName("DELETE: Deve deletar com sucesso")
    void testShouldDeleteUserSuccessfully() {
        Long id = 1L;
        when(userInfoRepository.findById(id)).thenReturn(Optional.of(new UserInfo()));

        userInfoService.delete(id);

        verify(userInfoRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("DELETE: Deve falhar ao deletar ID inexistente")
    void testShouldFailDeleteUserWithNonExistingId() {
        when(userInfoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userInfoService.delete(1L));
    }

    private UserInfoDTO createDTO() {
        return new UserInfoDTO(1L, "ana@ufrn.br", "Ana Costa", "anacosta", "senha123", Role.ALUNO);
    }
}