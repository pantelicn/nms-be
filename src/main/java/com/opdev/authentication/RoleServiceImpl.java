package com.opdev.authentication;

import com.opdev.model.user.Role;
import com.opdev.repository.RoleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    @Override
    public Role findByName(final String name) {
        return roleRepository.findByName(name);
    }

}
