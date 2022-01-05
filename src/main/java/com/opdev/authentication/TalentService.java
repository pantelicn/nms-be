package com.opdev.authentication;

import com.opdev.model.talent.Talent;

import java.util.Optional;

public interface TalentService {

    Talent register(final Talent talent);

    Talent getByUsername(final String username);

    Optional<Talent> findByUsername(final String username);

    Talent view(final String username);

    Talent updateBasicInfo(final Talent talent);

    void disable(final String username);

    void delete(final String username);

    Talent getById(Long id);

}
