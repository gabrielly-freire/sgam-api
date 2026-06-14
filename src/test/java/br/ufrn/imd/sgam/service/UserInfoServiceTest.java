package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.UserInfoDTO;
import br.ufrn.imd.sgam.enums.Role;
import br.ufrn.imd.sgam.exception.BusinessException;
import br.ufrn.imd.sgam.exception.ResourceNotFoundException;
import br.ufrn.imd.sgam.mapper.UserInfoMapper;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.repository.UserInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @Mock
    private UserInfoRepository repository;

    @Mock
    private UserInfoMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserInfoService service;

    @Test
    void shouldSaveUserWithSolicitanteRole() {
        UserInfoDTO dto = createDTO();
        UserInfo model = new UserInfo();

        when(repository.existsUserInfoByEmail(dto.email())).thenReturn(false);
        when(repository.existsUserInfoByUsername(dto.username())).thenReturn(false);
        when(mapper.toUserInfo(dto)).thenReturn(model);
        when(passwordEncoder.encode(any())).thenReturn("hash");
        when(repository.save(any())).thenReturn(model);
        when(mapper.toUserInfoDTO(model)).thenReturn(dto);

        service.save(dto);

        verify(repository).save(argThat(user ->
                user.getRole() == Role.SOLICITANTE &&
                user.getPassword().equals("hash")
        ));
    }

    @Test
    void shouldSoftDeleteUser() {
        UserInfo user = new UserInfo();

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        service.delete(1L);

        verify(repository).save(argThat(u -> !u.getActive()));
    }

    @Test
    void shouldPromoteToCoordenador() {
        UserInfo user = new UserInfo();
        user.setRole(Role.SOLICITANTE);

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);
        when(mapper.toUserInfoDTO(user)).thenReturn(createDTO());

        service.tornarCoordenador(1L);

        assertEquals(Role.COORDENADOR, user.getRole());
    }

    @Test
    void shouldFailPromoteIfAlreadyCoordinator() {
        UserInfo user = new UserInfo();
        user.setRole(Role.COORDENADOR);

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class,
                () -> service.tornarCoordenador(1L));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.get(1L));
    }

    private UserInfoDTO createDTO() {
        return new UserInfoDTO(
                1L,
                "ana@ufrn.br",
                "Ana",
                "ana",
                "123456",
                Role.SOLICITANTE
        );
    }
}