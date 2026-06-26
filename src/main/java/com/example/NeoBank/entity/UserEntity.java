package com.example.NeoBank.entity;

import com.example.NeoBank.dto.UserDto;
import com.example.NeoBank.enums.OccupationEnum;
import com.example.NeoBank.enums.Role;
import com.example.NeoBank.enums.TypeAccountEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "User_entity")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private Date dateNasciment;

    @Column(nullable = false)
    private Double salary; //renda mensal

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OccupationEnum occupationEnum;

    @Enumerated(EnumType.STRING)
    private TypeAccountEnum typeAccountEnum;



    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user")
    private AccountEntity account;

    public UserEntity(UserDto userDto) {
        this.name = userDto.name();
        this.email = userDto.email();
        this.password = userDto.password();
        this.occupationEnum = userDto.occupationEnum();
        this.cpf = userDto.cpf();
        this.phone = userDto.phone();
        this.salary = userDto.salary();
        this.typeAccountEnum = userDto.typeAccountEnum();
        this.dateNasciment = userDto.dateNasciment();
    }

    @Override
    // Retorna as autoridades (roles/permissions) associadas ao usuário.
    // Spring Security usa objetos GrantedAuthority para representar permissões.
    // Atualmente o método retorna uma lista vazia, o que significa que o
    // usuário não terá autoridades atribuídas. Em implementações reais
    // normalmente mapeamos o campo `role` para uma coleção de
    // SimpleGrantedAuthority, por exemplo:
    // return List.of(new SimpleGrantedAuthority(role.name()));
    // Ou retornar várias authorities conforme necessário.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    // Retorna o nome de usuário usado pela autenticação.
    // Spring Security usa esse valor como identificador do principal.
    // Aqui está vazio (""), mas o correto normalmente é retornar um
    // identificador único, por exemplo o `email` do usuário:
    // return this.email;
    public String getUsername() {
        return email;
    }
    @Override
    public String getPassword() {
        return password;
    }


    @Override
    // Indica se a conta do usuário expirou.
    // Se retornar true, a conta NÃO está expirada e pode ser usada.
    // Em vez de delegar ao default, você pode usar um campo da entidade
    // para controlar expiração, por exemplo `accountExpirationDate`.
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    // Indica se a conta do usuário está bloqueada.
    // Se retornar true, a conta NÃO está bloqueada. Em cenários reais
    // você pode controlar esse estado com um campo (ex: `locked`) e
    // retornar !locked.
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    // Indica se as credenciais (senha) do usuário expiraram.
    // Se retornar true, as credenciais NÃO estão expiradas e podem ser usadas.
    // Use isto para forçar mudança de senha após algum tempo, se necessário.
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    // Indica se a conta do usuário está habilitada.
    // Se retornar true, o usuário pode autenticar-se; se false, mesmo com
    // credenciais válidas o acesso será negado. Pode ser usado para controlar
    // ativação por e-mail ou suspensão temporária.
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
