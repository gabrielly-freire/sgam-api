package br.ufrn.imd.sgam.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import br.ufrn.imd.sgam.enums.Role;

import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserInfoRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String email = "admin@admin.com";

        if (!repository.existsUserInfoByEmail(email)) {

            UserInfo admin = new UserInfo();
            admin.setName("Administrador");
            admin.setEmail(email);
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);

            repository.save(admin);

            System.out.println("✅ Usuário ADMIN criado!");
        }
    }
}
