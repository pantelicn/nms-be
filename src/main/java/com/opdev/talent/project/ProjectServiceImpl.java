package com.opdev.talent.project;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.exception.ApiBadRequestException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.talent.Project;
import com.opdev.model.talent.Talent;
import com.opdev.repository.ProjectRepository;
import com.opdev.talent.TalentService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository repository;
    private final TalentService talentService;

    @Override
    @Transactional
    public Project create(@NonNull final String description,
                          @NonNull final String technologiesUsed,
                          @NonNull final String myRole,
                          @NonNull final String talentUsername,
                          @NonNull final Instant startDate,
                          final Instant endDate) {
        Talent foundTalent = talentService.getByUsername(talentUsername);
        return repository.save(Project.builder()
                                       .description(description)
                                       .technologiesUsed(technologiesUsed)
                                       .myRole(myRole)
                                       .talent(foundTalent)
                                       .startDate(startDate)
                                       .endDate(endDate)
                                       .build());
    }

    @Override
    @Transactional
    public Project edit(@NonNull Long id,
                        @NonNull final String description,
                        @NonNull final String technologiesUsed,
                        @NonNull final String myRole,
                        @NonNull final String username,
                        @NonNull final Instant startDate,
                        final Instant endDate) {
        validateOwnership(id, username);
        Project found = repository.findById(id).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found")
                .entity("Project")
                .id(id.toString()).build());

        found.setDescription(description);
        found.setMyRole(myRole);
        found.setTechnologiesUsed(technologiesUsed);
        found.setStartDate(startDate);
        found.setEndDate(endDate);

        return repository.save(found);
    }

    @Override
    @Transactional
    public void remove(final Long id, final String username) {
        validateOwnership(id, username);
        repository.deleteById(id);
    }

    private void validateOwnership(final Long id, final String username) {
        Talent foundTalent = talentService.getByUsername(username);
        if (!repository.existsByIdAndTalent(id, foundTalent)) {
            throw new ApiBadRequestException("User is not owner of project!");
        }
    }
}
