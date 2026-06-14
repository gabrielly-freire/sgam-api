package br.ufrn.imd.sgam.mapper;

import br.ufrn.imd.sgam.dto.UserInfoDTO;
import br.ufrn.imd.sgam.model.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserInfoMapper {

    public UserInfo toUserInfo(UserInfoDTO userInfo) {
        UserInfo user = new UserInfo();
        user.setId(userInfo.id());
        user.setEmail(userInfo.email());
        user.setName(userInfo.name());
        user.setUsername(userInfo.username());
        user.setPassword(userInfo.password());
        user.setRole(userInfo.role());

        return user;
    }

    public UserInfoDTO toUserInfoDTO(UserInfo user) {
        return new UserInfoDTO(user.getId(),
                user.getEmail(),
                user.getName(),
                user.getUsername(),
                user.getPassword(),
                user.getRole());
    }
}
