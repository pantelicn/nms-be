package com.opdev.test;

import java.util.Arrays;
import java.util.List;

import com.opdev.config.security.Roles;
import com.opdev.model.user.Role;
import com.opdev.model.user.User;
import com.opdev.model.user.UserRole;
import com.opdev.model.user.UserType;
import com.opdev.repository.RoleRepository;
import com.opdev.repository.UserRepository;
import com.opdev.repository.UserRoleRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
class InitDataServiceImpl implements InitDataService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    @Override
    public void insert() {
        if (roleRepository.count() != 0) {
            LOGGER.warn("Skipping the import of the init data");
            return;
        }

        final Role roleAdmin = Role.builder().name(Roles.ADMIN).build();
        final Role roleTalent = Role.builder().name(Roles.TALENT).build();
        final Role roleCompany = Role.builder().name(Roles.COMPANY).build();
        final List<Role> roles = Arrays.asList(roleAdmin, roleTalent, roleCompany);
        roles.forEach(roleRepository::save);

        final User userGoran = User.builder().username("gox69@opdev.rs").password(passwordEncoder.encode("rav4"))
                .enabled(Boolean.TRUE).type(UserType.TALENT).build();
        final User userNikola = User.builder().username("znikola@xxx.xxx").password(passwordEncoder.encode("corolla"))
                .enabled(Boolean.TRUE).type(UserType.TALENT).build();
        final List<User> users = Arrays.asList(userGoran, userNikola);
        users.forEach(userRepository::save);

        final UserRole nikolaAdmin = UserRole.builder().role(roleAdmin).user(userNikola).build();
        final UserRole goranAdmin = UserRole.builder().role(roleAdmin).user(userGoran).build();

        userRoleRepository.save(nikolaAdmin);
        userRoleRepository.save(goranAdmin);

        LOGGER.warn("Init data imported.");
    }
}
