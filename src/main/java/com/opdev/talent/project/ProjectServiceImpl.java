package com.opdev.talent.project;

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
                          @NonNull final String talentUsername) {
        Talent foundTalent = talentService.getByUsername(talentUsername);
        return repository.save(Project.builder()
                                       .description(description)
                                       .technologiesUsed(technologiesUsed)
                                       .myRole(myRole)
                                       .talent(foundTalent)
                                       .build());
    }

    @Override
    @Transactional
    public Project edit(@NonNull Long id,
                        @NonNull final String description,
                        @NonNull final String technologiesUsed,
                        @NonNull final String myRole,
                        @NonNull final String username) {
        validateOwnership(id, username);
        Project found = repository.findById(id).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found")
                .entity("Project")
                .id(id.toString()).build());

        found.setDescription(description);
        found.setMyRole(myRole);
        found.setTechnologiesUsed(technologiesUsed);

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
