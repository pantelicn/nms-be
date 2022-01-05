package com.opdev.position;

import com.opdev.authentication.UserService;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.talent.Position;
import com.opdev.model.user.User;
import com.opdev.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Position add(final Position newPosition) {
        Objects.requireNonNull(newPosition);
        final User loggedUser = userService.getLoggedInUser();
        newPosition.setCreatedBy(loggedUser);
        newPosition.setModifiedBy(loggedUser);
        final Position saved = positionRepository.save(newPosition);
        LOGGER.info("New position {} has been added.", saved.toString());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Position get(final String code) {
        Objects.requireNonNull(code);
        final Optional<Position> found = positionRepository.findByCode(code);
        return found.orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found")
                .entity("Position")
                .id(code).build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Position> find(final Pageable pageable) {
        Objects.requireNonNull(pageable);
        return positionRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Position edit(Position modified) {
        Objects.requireNonNull(modified);
        checkIfExist(modified.getId()); // this line of code will check if Position exist.
        modified.setModifiedBy(userService.getLoggedInUser());
        final Position newPosition = positionRepository.save(modified);
        LOGGER.info("Position with id {} is modified {}", modified.getId(), modified.toString());
        return newPosition;
    }

    @Override
    @Transactional
    public void remove(final String code) {
        Objects.requireNonNull(code);
        get(code); // this line of code will check if Position exist.
        positionRepository.deleteByCode(code);
        LOGGER.info("Position with code {} has been deleted.", code);
    }

    private void checkIfExist(final Long id) {
        Objects.requireNonNull(id);
        Optional<Position> found = positionRepository.findById(id);
        found.orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found")
                .entity("Position")
                .id(id.toString()).build());
    }

}
