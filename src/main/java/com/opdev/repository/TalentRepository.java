package com.opdev.repository;

import java.util.List;
import java.util.Optional;

import com.opdev.model.talent.Talent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TalentRepository extends JpaRepository<Talent, Long>, JpaSpecificationExecutor<Talent> {

    Optional<Talent> findByUserUsername(final String username);

    @Query(
            value = "select * from TALENT T " +
                    "join LOCATION L on T.ID = L.TALENT_ID " +
                    "where lower(L.COUNTRY) = lower(:country) " +
                    "order by T.CREATED_ON desc limit 10",
            nativeQuery = true
    )
    List<Talent> findLatest10ByCountry(@Param("country") final String country);

}
