package com.opdev.talent;


import com.opdev.exception.ApiEmailExistsException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.location.AvailableLocation;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.User;
import com.opdev.model.user.UserRole;
import com.opdev.repository.TalentRepository;
import com.opdev.talent.search.TalentSpecification;
import com.opdev.user.UserService;
import com.opdev.user.role.RoleService;
import com.opdev.user.userole.UserRoleService;
import com.opdev.user.verification.VerificationTokenService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class TalentServiceImpl implements TalentService {

    private final UserService userService;
    private final TalentRepository talentRepository;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final VerificationTokenService verificationTokenService;


    @Transactional
    @Override
    public Talent register(@NonNull final Talent talent) {
        validateTalent(talent);
        talent.getCurrentLocation().setTalent(talent);
        User talentUser = userService.save(talent.getUser());
        verificationTokenService.create(talentUser.getVerificationToken());
        UserRole talentUserRole = UserRole.builder()
                .role(roleService.getTalentRole())
                .user(talentUser)
                .build();
        userRoleService.create(talentUserRole);
        Talent created = talentRepository.save(talent);
        return created;
    }

    @Override
    public Talent save(@NonNull Talent talent) {
        return talentRepository.save(talent);
    }

    @Transactional(readOnly = true)
    @Override
    public Talent getByUsername(@NonNull final String username) {
        return findByUsername(username).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .entity(Talent.class.getSimpleName()).id(username).build());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Talent> findByUsername(@NonNull final String username) {
        return talentRepository.findByUserUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Talent view(@NonNull final String username) {
        final Talent talent = getByUsername(username);
        if (!userService.isAdminLoggedIn()) {
            userService.ensureIsEnabled(talent.getUser());
        }

        return talent;
    }

    @Transactional
    @Override
    public Talent updateBasicInfo(@NonNull final Talent updatedTalent) {
        if (!userService.isAdminLoggedIn()) {
            userService.ensureIsEnabled(updatedTalent.getUser());
        }
        return talentRepository.save(updatedTalent);
    }

    @Transactional
    @Override
    public void disable(@NonNull final String username) {
        final Talent talent = getByUsername(username);

        final User updatedUser = talent.getUser().toBuilder().enabled(Boolean.FALSE).build();
        updatedUser.setModifiedBy(userService.resolveModifiedBy(updatedUser));

        final Talent disabledTalent = talent.toBuilder().user(updatedUser).build();
        userService.save(updatedUser);
        talentRepository.save(disabledTalent);
    }

    @Transactional
    @Override
    public void delete(@NonNull final String username) {
        final Talent talent = getByUsername(username);

        if (!userService.isAdminLoggedIn()) {
            userService.ensureIsEnabled(talent.getUser());
        }

        talentRepository.delete(talent);
        userService.delete(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Talent getById(@NonNull final Long id) {
        return talentRepository.findById(id).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .entity(Talent.class.getSimpleName())
                .id(id.toString())
                .build());
    }

    private void validateTalent(@NonNull final Talent talent) throws ApiEmailExistsException {
        final Optional<User> existingUser = userService.findByUsername(talent.getUser().getUsername());
        if (existingUser.isPresent()) {
            LOGGER.info("The username already exists: {}", talent.getUser().getUsername());
            // TODO: handle this exception in the global message handler, and return 201
            throw ApiEmailExistsException.builder().message("User.already.exists").entity(Talent.class.getSimpleName())
                    .id(talent.getUser().getUsername()).build();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Talent> findLatest10ByCountry(@NonNull final String country) {
        return talentRepository.findLatest10ByCountry(country);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Talent> find(TalentSpecification specification, Pageable pageable) {
        Objects.requireNonNull(pageable);
        return talentRepository.findAll(specification, pageable);
    }

    @Transactional
    @Override
    public Talent addAvailableLocation(@NonNull Talent oldTalent, @NonNull AvailableLocation availableLocation) {
        oldTalent.getAvailableLocations().add(availableLocation);
        oldTalent.setModifiedOn(Instant.now());

        Talent updated = talentRepository.save(oldTalent);
        LOGGER.info("Added new available location on talent {}: {}", updated.getUser().getUsername(), availableLocation);
        return updated;
    }

    @Transactional
    @Override
    public Talent removeAvailableLocation(@NonNull Talent oldTalent, @NonNull Long id) {
        boolean removed = oldTalent.getAvailableLocations()
                .removeIf(availableLocation -> availableLocation.getId().equals(id));
        ApiEntityNotFoundException.builder().entity("AvailableCountry").id(id.toString()).build()
                .throwIf(() -> !removed);

        Talent updated = talentRepository.save(oldTalent);
        LOGGER.info("Removed available location with id {} on talent {}", id,  updated.getUser().getUsername());
        return updated;
    }

}
