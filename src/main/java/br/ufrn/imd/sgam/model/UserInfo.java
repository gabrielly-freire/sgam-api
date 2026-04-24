package br.ufrn.imd.sgam.model;

import br.ufrn.imd.sgam.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority; 
import org.springframework.security.core.authority.SimpleGrantedAuthority; 
import org.springframework.security.core.userdetails.UserDetails; 

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user_info", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@SQLRestriction(value = "active = true")
public class UserInfo extends BaseEntity implements UserDetails {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    private String email;

    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Conta não expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Conta não bloqueia
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Senha não expira
    }

    @Override
    public boolean isEnabled() {
        return true; // Usuário ativo
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converte o Role do SGAM para o formato que o Spring Security entende
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }
}
