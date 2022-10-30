package com.opdev.user.role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.model.user.Role;
import com.opdev.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Role getTalentRole() {
        return repository.findByName("ROLE_TALENT");
    }

    @Override
    @Transactional(readOnly = true)
    public Role getCompanyRole() {
        return repository.findByName("ROLE_COMPANY");
    }

}
