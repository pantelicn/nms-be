package com.opdev.talent;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.aws.cognito.CognitoService;
import com.opdev.exception.ApiEmailExistsException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.User;
import com.opdev.repository.TalentRepository;
import com.opdev.user.UserService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class TalentServiceImpl implements TalentService {

    private final UserService userService;
    private final TalentRepository talentRepository;
    private final CognitoService cognitoService;

    @Transactional
    @Override
    public Talent register(@NonNull final Talent talent) {
        validateTalent(talent);
        talent.getCurrentLocation().setTalent(talent);
        userService.save(talent.getUser());
        Talent created = talentRepository.save(talent);
        cognitoService.createTalent(talent.getUser().getUsername(), talent.getUser().getPassword());
        return created;
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

}
