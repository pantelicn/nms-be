package com.opdev.talent.availablelocation;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.location.TalentAvailableLocation;
import com.opdev.model.talent.Talent;
import com.opdev.repository.TalentAvailableLocationRepository;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@Service
@AllArgsConstructor
public class AvailableLocationServiceImpl implements AvailableLocationService {

    private final TalentAvailableLocationRepository repository;

    @Override
    @Transactional
    public TalentAvailableLocation create(@NonNull final TalentAvailableLocation newAvailableLocation) {
        return repository.save(newAvailableLocation);
    }

    @Override
    @Transactional
    public TalentAvailableLocation addCity(final Long id, final String city, final Talent talent) {
        TalentAvailableLocation found = findByIdAndTalent(id, talent);
        found.addCity(city);
        found.setModifiedOn(Instant.now());
        return repository.save(found);
    }

    @Override
    @Transactional
    public TalentAvailableLocation removeCity(final Long id, final String city, final Talent talent) {
        TalentAvailableLocation found = findByIdAndTalent(id, talent);
        found.removeCity(city);
        found.setModifiedOn(Instant.now());
        return found;
    }

    @Override
    @Transactional
    public void remove(final Long id, final Talent talent) {
        repository.deleteByIdAndTalent(id, talent);
    }

    private TalentAvailableLocation findByIdAndTalent(Long id, Talent talent) {
        return repository.findByIdAndTalent(id, talent).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message(String.format("Entity.not.found for id %s and talent id %s", id, talent.getId()))
                .entity("TalentAvailableLocation")
                .id(id.toString()).build());
    }

}
