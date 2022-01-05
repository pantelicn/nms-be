package com.opdev.term;

import com.opdev.authentication.UserService;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.term.Term;
import com.opdev.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermServiceImpl implements TermService {

    private final TermRepository repository;

    private final UserService userService;

    @Override
    @Transactional
    public Term add(final Term newTerm) {
        Objects.requireNonNull(newTerm);
        return repository.save(newTerm);
    }

    @Override
    @Transactional(readOnly = true)
    public Term get(String code) {
        Objects.requireNonNull(code);
        return repository.findByCode(code).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found")
                .entity("Term")
                .id(code).build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Term> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Term edit(final Term modified) {
        Objects.requireNonNull(modified);
        final Term existing = get(modified.getCode());
        existing.update(modified, userService.getLoggedInUser());
        final Term newTerm = repository.save(existing);
        LOGGER.info("Term with code {} is modified {}", modified.getCode(), modified.toString());
        return newTerm;
    }

    @Override
    @Transactional
    public void remove(final String code) {
        Objects.requireNonNull(code);
        Term found = get(code);
        repository.delete(found);
        LOGGER.info("Term with code {} has been deleted", code);
    }

}
