package br.ufrn.imd.sgam.repository;

import br.ufrn.imd.sgam.model.UserInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends GenericRepository<UserInfo> {

    boolean existsUserInfoByEmail(String email);

    boolean existsUserInfoByUsername(String username);

}
