package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.UserInfoDTO;
import br.ufrn.imd.sgam.enums.Role;
import br.ufrn.imd.sgam.exception.BusinessException;
import br.ufrn.imd.sgam.exception.ResourceNotFoundException;
import br.ufrn.imd.sgam.mapper.UserInfoMapper;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final UserInfoMapper userInfoMapper;
    private final PasswordEncoder passwordEncoder;

    public UserInfoDTO save(UserInfoDTO dto) {

        if (userInfoRepository.existsUserInfoByEmail(dto.email())) {
            throw new BusinessException("Já existe um usuário com este email.", HttpStatus.CONFLICT);
        }

        if (userInfoRepository.existsUserInfoByUsername(dto.username())) {
            throw new BusinessException("Já existe um usuário com este username.", HttpStatus.CONFLICT);
        }

        UserInfo user = userInfoMapper.toUserInfo(dto);

        user.setPassword(passwordEncoder.encode(dto.password()));

        user.setRole(Role.SOLICITANTE);

        user = userInfoRepository.save(user);

        return userInfoMapper.toUserInfoDTO(user);
    }

    public UserInfoDTO get(Long id) {
        UserInfo user = userInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return userInfoMapper.toUserInfoDTO(user);
    }

    public Page<UserInfoDTO> list(Pageable pageable) {
        return userInfoRepository.findAllPage(pageable)
                .map(userInfoMapper::toUserInfoDTO);
    }

    public UserInfoDTO update(Long id, UserInfoDTO dto) {

        UserInfo user = userInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!user.getEmail().equals(dto.email()) &&
                userInfoRepository.existsUserInfoByEmail(dto.email())) {
            throw new BusinessException("Já existe um usuário com este email.", HttpStatus.CONFLICT);
        }

        if (!user.getUsername().equals(dto.username()) &&
                userInfoRepository.existsUserInfoByUsername(dto.username())) {
            throw new BusinessException("Já existe um usuário com este username.", HttpStatus.CONFLICT);
        }

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setUsername(dto.username());
        user.setRole(dto.role());

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        user = userInfoRepository.save(user);

        return userInfoMapper.toUserInfoDTO(user);
    }

    public void delete(Long id) {

        UserInfo user = userInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // 🧠 soft delete
        user.setActive(false);

        userInfoRepository.save(user);
    }

    public UserInfoDTO tornarCoordenador(Long id) {

        UserInfo user = userInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (user.getRole() == Role.COORDENADOR) {
            throw new BusinessException("Usuário já é coordenador.", HttpStatus.CONFLICT);
        }

        user.setRole(Role.COORDENADOR);

        user = userInfoRepository.save(user);

        return userInfoMapper.toUserInfoDTO(user);
    }
}