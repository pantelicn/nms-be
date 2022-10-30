package com.opdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.user.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

}
