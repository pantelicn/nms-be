package com.opdev.skill;

import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.talent.Skill;
import com.opdev.model.talent.SkillStatus;
import com.opdev.model.user.User;
import com.opdev.repository.SkillRepository;
import com.opdev.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository repository;
    private final UserService userService;

    @Override
    @Transactional
    public Skill add(final Skill newSkill) {
        Objects.requireNonNull(newSkill);
        final User loggedUser = userService.getLoggedInUser();
        newSkill.setCreatedBy(loggedUser);
        newSkill.setModifiedBy(loggedUser);
        final Skill created = repository.save(newSkill);
        LOGGER.info("New skill {} has been added.", newSkill.toString());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public Skill get(final String code) {
        Objects.requireNonNull(code);
        return repository.findByCode(code).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found")
                .entity("Skill")
                .id(code).build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Skill> find(final Specification<Skill> skillSpec, final Pageable pageable) {
        Objects.requireNonNull(pageable);
        return repository.findAll(skillSpec, pageable);
    }

    @Override
    @Transactional
    public Skill edit(final Skill modified) {
        Objects.requireNonNull(modified);
        get(modified.getId());
        modified.setModifiedBy(userService.getLoggedInUser());
        final Skill newSkill = repository.save(modified);
        LOGGER.info("Skill with id {} is modified {}", modified.getId(), modified.toString());
        return newSkill;
    }

    @Override
    @Transactional
    public void remove(final String code) {
        Objects.requireNonNull(code);
        get(code);
        repository.deleteByCode(code);
        LOGGER.info("Skill with code {} has been deleted", code);
    }

    @Override
    @Transactional
    public Skill updateStatus(final String code, final SkillStatus status) {
        Objects.requireNonNull(code);
        Objects.requireNonNull(status);
        final Skill found = get(code);
        found.setStatus(status);
        final Skill modified = repository.save(found);
        LOGGER.info("Skill with code {} has been approved", code);
        return modified;
    }

    private Skill get(final Long id) {
        Objects.requireNonNull(id);
        return repository.findById(id).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found")
                .entity("Skill")
                .id(id.toString()).build());
    }

}
