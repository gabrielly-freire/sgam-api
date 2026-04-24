package br.ufrn.imd.sgam.model;

import br.ufrn.imd.sgam.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@Table(name = "user_info", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@SQLRestriction(value = "active = true")
public class UserInfo extends BaseEntity {

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
}
