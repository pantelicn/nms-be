package com.opdev.model.user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.Email;

import com.opdev.model.Audit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Getter
@Entity
@Table(name = "user_table")
public class User extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Email
    @Column(nullable = false, unique = true)
    private String username;

    @Column
    @Setter
    private String password;

    @NonNull
    @Column(nullable = false)
    @Builder.Default
    @Setter
    private Boolean enabled = Boolean.FALSE;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Setting> settings = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "verification_token_id", referencedColumnName = "id")
    private VerificationToken verificationToken;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false)
    private AuthType authType;

    public boolean isCompany() {
        return userRoles.stream().filter(userRole -> userRole.getRole().getName().equals("ROLE_COMPANY")).collect(Collectors.toList()).size() == 1;
    }

    public boolean isTalent() {
        return userRoles.stream().filter(userRole -> userRole.getRole().getName().equals("ROLE_TALENT")).collect(Collectors.toList()).size() == 1;
    }

}
