package com.opdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.user.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

}
