package com.opdev.authentication;

import com.opdev.model.user.Role;

public interface RoleService {
    Role findByName(final String name);
}
