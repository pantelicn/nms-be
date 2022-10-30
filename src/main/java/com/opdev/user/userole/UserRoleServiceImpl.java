package com.opdev.user.userole;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.model.user.UserRole;
import com.opdev.repository.UserRoleRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository repository;

    @Override
    @Transactional
    public UserRole create(@NonNull final UserRole userRole) {
        return repository.save(userRole);
    }

}
