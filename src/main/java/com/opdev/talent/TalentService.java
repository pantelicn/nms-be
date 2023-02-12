package com.opdev.talent;

import com.opdev.model.talent.Talent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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

    Page<Talent> findAll(Pageable pageable);

    Page<Talent> find(final Specification<Talent> specification, final Pageable pageable);

    Page<Talent> findWithoutExistingActiveRequest(List<Long> pendingOrAcceptedTalentIds,
                                                  Specification<Talent> searchSpecification,
                                                  Pageable pageable);

    void updateAvailability(Talent talent, boolean available);

}
