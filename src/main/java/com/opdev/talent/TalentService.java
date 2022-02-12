package com.opdev.talent;

import java.util.List;
import java.util.Optional;

import com.opdev.model.talent.Talent;

public interface TalentService {

    Talent register(final Talent talent);

    Talent getByUsername(final String username);

    Optional<Talent> findByUsername(final String username);

    Talent view(final String username);

    Talent updateBasicInfo(final Talent talent);

    void disable(final String username);

    void delete(final String username);

    Talent getById(Long id);

    List<Talent> findLatest10ByCountry(final String country);

}
