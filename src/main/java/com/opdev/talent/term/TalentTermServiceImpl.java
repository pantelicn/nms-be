package com.opdev.talent.term;

import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.talent.Talent;
import com.opdev.model.term.TalentTerm;
import com.opdev.model.term.Term;
import com.opdev.repository.TalentTermRepository;
import com.opdev.talent.TalentService;
import com.opdev.term.TermService;
import com.opdev.term.validation.TalentTermValidator;
import com.opdev.user.UserService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TalentTermServiceImpl implements TalentTermService {

    private final TalentService talentService;
    private final TermService termService;
    private final UserService userService;
    private final TalentTermRepository repository;

    @Override
    @Transactional
    public List<TalentTerm> addTermsToTalent(
            @NonNull final String username,
            @NonNull final List<TalentTerm> talentTerms) {
        final Talent foundTalent = talentService.getByUsername(username);
        return talentTerms.stream()
                .map(talentTerm -> create(talentTerm, foundTalent))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TalentTerm get(@NonNull final Long id) {
        return repository.findById(id).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found")
                .entity("TalentTerm")
                .id(id.toString()).build());
    }

    @Override
    @Transactional
    public List<TalentTerm> edit(@NonNull final List<TalentTerm> modifiedList, @NonNull final String username) {
        Map<Long, TalentTerm> existing = getByTalent(username).stream()
                .collect(Collectors.toMap(TalentTerm::getId, Function.identity()));
        for (TalentTerm modified: modifiedList) {
            modified.setModifiedBy(userService.getLoggedInUser());
            modified.setTalent(existing.get(modified.getId()).getTalent());
            modified.setTerm(existing.get(modified.getId()).getTerm());
        }
        List<TalentTerm> updatedList = repository.saveAll(modifiedList);
        LOGGER.info("TalentTerms modified: ", existing.keySet());
        return updatedList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TalentTerm> getByTalent(@NonNull final String username) {
        return repository.findByTalentUserUsername(username);
    }

    @Override
    @Transactional
    public void remove(@NonNull final Long id, @NonNull final String username) {
        getByIdAndTalent(id, username);
        repository.deleteById(id);
        LOGGER.info("TalentTerm with id {} has been removed.", id);
    }

    @Override
    @Transactional(readOnly = true)
    public TalentTerm getByIdAndTalent(@NonNull final Long id, @NonNull final String username) {
        return repository.findByIdAndTalentUserUsername(id, username)
                .orElseThrow(() -> ApiEntityNotFoundException.builder()
                    .message("Entity.not.found")
                    .entity("TalentTerm")
                    .id(id.toString()).build());
    }

    private TalentTerm create(TalentTerm talentTerm, Talent talent) {
        final Term foundTerm = termService.get(talentTerm.getTerm().getCode());
        final TalentTerm newTalentTerm = TalentTerm.builder()
                .value(talentTerm.getValue())
                .negotiable(talentTerm.getNegotiable())
                .unitOfMeasure(talentTerm.getUnitOfMeasure())
                .talent(talent)
                .term(foundTerm).build();
        validate(newTalentTerm);
        TalentTerm created = repository.save(newTalentTerm);
        LOGGER.info("New talent term {} has been added.", created);
        return created;
    }

    private void validate(TalentTerm talentTerm) {
        TalentTermValidator.validate(talentTerm);
    }

}
