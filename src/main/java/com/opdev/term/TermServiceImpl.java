package com.opdev.term;

import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.term.Term;
import com.opdev.repository.TermRepository;
import com.opdev.user.UserService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.domain.Specification;
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

    private Term get(@NonNull Long id) {
        return repository.findById(id).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found")
                .entity("Term")
                .id(id.toString()).build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Term> findAll(Specification<Term> termSpec) {
        return repository.findAll(termSpec);
    }

    @Override
    @Transactional
    public Term edit(final Term modified) {
        Objects.requireNonNull(modified);
        final Term existing = get(modified.getId());
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
