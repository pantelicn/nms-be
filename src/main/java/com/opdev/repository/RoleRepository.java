package com.opdev.repository;

import com.opdev.model.user.Role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(final String name);
}
