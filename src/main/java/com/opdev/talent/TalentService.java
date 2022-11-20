package com.opdev.talent;

import com.opdev.model.location.AvailableLocation;
import com.opdev.model.talent.Talent;
import com.opdev.talent.search.TalentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface TalentService {

    Talent register(final Talent talent);

    Talent save(final Talent talent);

    Talent getByUsername(final String username);

    Optional<Talent> findByUsername(final String username);

    Talent view(final String username);

    Talent updateBasicInfo(final Talent talent);

    void disable(final String username);

    void delete(final String username);

    Talent getById(Long id);

    List<Talent> findLatest10ByCountry(final String country);

    Page<Talent> find(final TalentSpecification specification, final Pageable pageable);

    Talent addAvailableLocation(Talent oldTalent, AvailableLocation availableLocation);

    Talent removeAvailableLocation(Talent oldTalent, Long id);

}
