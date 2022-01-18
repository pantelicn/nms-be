package com.opdev.test;

import java.util.Arrays;
import java.util.List;

import com.opdev.config.security.Roles;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.repository.UserRepository;

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

    @Transactional
    @Override
    public void insert() {

        final User userGoran = User.builder().username("gox69@opdev.rs").password(passwordEncoder.encode("rav4"))
                .enabled(Boolean.TRUE).type(UserType.TALENT).build();
        final User userNikola = User.builder().username("znikola@xxx.xxx").password(passwordEncoder.encode("corolla"))
                .enabled(Boolean.TRUE).type(UserType.TALENT).build();
        final List<User> users = Arrays.asList(userGoran, userNikola);
        users.forEach(userRepository::save);

        LOGGER.warn("Init data imported.");
    }
}
