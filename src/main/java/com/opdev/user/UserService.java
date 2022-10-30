package com.opdev.user;

import java.util.Optional;
import java.util.UUID;

import com.opdev.exception.ApiEntityDisabledException;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;

public interface UserService {

    Optional<User> findByUsername(String username);

    User getByUsername(String username);

    boolean isAdminLoggedIn();

    Optional<User> findLoggedInUser();

    User getLoggedInUser();

    void delete(String username);

    User save(User user);

    /**
     * Checks if the currently logged in user is an admin. If yes, the admin is
     * returned. Otherwise, the given user is returned.
     * 
     * @param user the user associated with the entity (talent or company) being
     *             updated.
     * @return a resolved user, set as the modified by user.
     */
    User resolveModifiedBy(User user);

    /**
     * Checks if the given {@code user} is enabled. If not, the
     * {@link ApiEntityDisabledException} is thrown.
     * 
     * @param user to check
     */
    void ensureIsEnabled(User user) throws ApiEntityDisabledException;

    Optional<User> findUserByUsernameAndType(String username, UserType type);

    void activateUser(UUID activationCode);

}
